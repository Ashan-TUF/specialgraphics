package uk.specialgraphics.api.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import uk.specialgraphics.api.config.Config;
import uk.specialgraphics.api.entity.*;
import uk.specialgraphics.api.exception.ErrorException;
import uk.specialgraphics.api.payload.request.*;
import uk.specialgraphics.api.payload.response.*;
import uk.specialgraphics.api.repository.*;
import uk.specialgraphics.api.service.CourseService;
import uk.specialgraphics.api.service.UserProfileService;
import uk.specialgraphics.api.utils.FileUploadUtil;
import uk.specialgraphics.api.utils.VarList;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class CourseServiceImpl implements CourseService {
    private final UserProfileService userProfileService;
    private final CourseRepository courseRepository;
    private final CourseSectionRepository courseSectionRepository;
    private final SectionCurriculumItemRepository sectionCurriculumItemRepository;
    private final CurriculumItemFileTypeRepository curriculumItemFileTypeRepository;
    private final CurriculumItemFileRepository curriculumItemFileRepository;
    private final QuizeRepository quizeRepository;
    private final QuizeItemRepository quizeItemRepository;
    private final AnswerRepository answerRepository;


    @Autowired
    public CourseServiceImpl(UserProfileService userProfileService,
                             CourseRepository courseRepository,
                             CourseSectionRepository courseSectionRepository,
                             SectionCurriculumItemRepository sectionCurriculumItemRepository,
                             CurriculumItemFileTypeRepository curriculumItemFileTypeRepository,
                             CurriculumItemFileRepository curriculumItemFileRepository,
                             QuizeRepository quizeRepository,
                             QuizeItemRepository quizeItemRepository,AnswerRepository answerRepository) {
        this.userProfileService = userProfileService;
        this.courseRepository = courseRepository;
        this.courseSectionRepository = courseSectionRepository;
        this.sectionCurriculumItemRepository = sectionCurriculumItemRepository;
        this.curriculumItemFileTypeRepository = curriculumItemFileTypeRepository;
        this.curriculumItemFileRepository = curriculumItemFileRepository;
        this.quizeRepository = quizeRepository;
        this.quizeItemRepository = quizeItemRepository;
        this.answerRepository=answerRepository;
    }


    private void authentication() {
        Authentication authentication;
        String username;
        GeneralUserProfile profile;
        authentication = SecurityContextHolder.getContext().getAuthentication();
        username = authentication.getName();
        profile = userProfileService.getProfile(username);

        if (profile == null)
            throw new ErrorException("User not found", VarList.RSP_NO_DATA_FOUND);

        if (profile.getIsActive() != 1)
            throw new ErrorException("User not active", VarList.RSP_NO_DATA_FOUND);

        if (profile.getGupType().getId() != 1)
            throw new ErrorException("You are not a instructor to this operation", VarList.RSP_NO_DATA_FOUND);
    }

    @Override
    public SuccessResponse addCourse(CourseRequest courseRequest) {
        authentication();
        final String courseTitle = courseRequest.getTitle();
        final MultipartFile image = courseRequest.getImg();
        final String video = courseRequest.getPromotionalVideo();
        final Double price = courseRequest.getPrice();
        final String description = courseRequest.getDescription();
        final String points = courseRequest.getPoints();

        if (courseTitle.isEmpty() || courseTitle == null) {
            throw new ErrorException("Please add a course title", VarList.RSP_NO_DATA_FOUND);
        } else if (image == null || image.isEmpty()) {
            throw new ErrorException("Please add a course's image", VarList.RSP_NO_DATA_FOUND);
        } else if (!image.getContentType().startsWith("image/") || !image.getOriginalFilename().matches(".*\\.(jpg|jpeg|png|gif|bmp|webp)$")) {
            throw new ErrorException("Invalid image file type or extension. Only image files are allowed.", VarList.RSP_NO_DATA_FOUND);
        } else if (video == null || video.isEmpty()) {
            throw new ErrorException("Please add a course's promotional video", VarList.RSP_NO_DATA_FOUND);
        } else if (price == null || price.toString().isEmpty()) {
            throw new ErrorException("Please add a price", VarList.RSP_NO_DATA_FOUND);
        } else if (description == null || description.isEmpty()) {
            throw new ErrorException("Please add a default price", VarList.RSP_NO_DATA_FOUND);
        } else if (points == null || points.isEmpty()) {
            throw new ErrorException("Please add points", VarList.RSP_NO_DATA_FOUND);
        }

        Course getCourse = courseRepository.getCourseByCourseTitle(courseTitle);
        if (getCourse != null) {
            throw new ErrorException("The course has already been added", VarList.RSP_NO_DATA_FOUND);
        }
        Course course = new Course();
        course.setCode(UUID.randomUUID().toString());
        course.setCourseTitle(courseTitle);
        course.setPromotionalVideo(video);
        try {
            FileUploadResponse imageUploadResponse = FileUploadUtil.saveFile(courseRequest.getImg(), "course-image");
            course.setImg(imageUploadResponse.getUrl());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        course.setCreatedDate(new Date());
        course.setPrice(price);
        course.setIsActive((byte) 1);
        courseRepository.save(course);

        SuccessResponse successResponse = new SuccessResponse();


        successResponse.setMessage("Course added successfully");
        successResponse.setVariable(VarList.RSP_SUCCESS);
        return successResponse;
    }

    @Override
    public List<CourseResponse> getAllCourses() {
        List<Course> courseList = courseRepository.findAll();
        List<CourseResponse> courseResponses = new ArrayList<>();
        for (Course course : courseList) {
            if (course.getIsActive() == 1) {
                CourseResponse courseResponse = new CourseResponse();
                courseResponse.setCode(course.getCode());
                courseResponse.setTitle(course.getCourseTitle());
                courseResponse.setImg(course.getImg());
                courseResponse.setPromotionalVideo(course.getPromotionalVideo());
                courseResponse.setCreatedDate(course.getCreatedDate());
                courseResponse.setBuyCount(course.getBuyCount());
                courseResponse.setDescription(course.getDescription());
                courseResponse.setPoints(course.getPoints());
                courseResponse.setPrice(course.getPrice());
                List<CourseSection> courseSections = courseSectionRepository.getCourseSectionByCourse(course);
                List<CourseSectionResponse> courseSectionResponses = new ArrayList<>();
                for (CourseSection courseSection : courseSections) {
                    CourseSectionResponse courseSectionResponse = new CourseSectionResponse();
                    courseSectionResponse.setSectionCode(courseSection.getSectionCode());
                    courseSectionResponse.setSectionName(courseSection.getSectionName());
                    List<SectionCurriculumItem> sectionCurriculumItems = sectionCurriculumItemRepository.getSectionCurriculumItemByCourseSection(courseSection);
                    List<CurriculumItemResponse> curriculumItemResponses = new ArrayList<>();
                    for (SectionCurriculumItem sectionCurriculumItem : sectionCurriculumItems) {
                        CurriculumItemResponse curriculumItemResponse = new CurriculumItemResponse();
                        curriculumItemResponse.setTitle(sectionCurriculumItem.getTitle());
                        curriculumItemResponse.setDescription(sectionCurriculumItem.getDescription());
                        curriculumItemResponse.setCurriculumItemType(sectionCurriculumItem.getCurriculumItemType());
                        curriculumItemResponses.add(curriculumItemResponse);
                    }
                    courseSectionResponse.setCurriculumItems(curriculumItemResponses);
                    courseSectionResponses.add(courseSectionResponse);
                }
                courseResponse.setCourseSections(courseSectionResponses);
                courseResponses.add(courseResponse);
            }
        }
        return courseResponses;

    }

    @Override
    public CourseResponse getCourseByCode(String courseCode) {
        Course course = courseRepository.getCourseByCode(courseCode);
        if (course == null) {
            throw new ErrorException("Invalid course code", VarList.RSP_NO_DATA_FOUND);
        }
        CourseResponse courseResponse = new CourseResponse();
        courseResponse.setCode(course.getCode());
        courseResponse.setTitle(course.getCourseTitle());
        courseResponse.setImg(course.getImg());
        courseResponse.setPromotionalVideo(course.getPromotionalVideo());
        courseResponse.setCreatedDate(course.getCreatedDate());
        courseResponse.setBuyCount(course.getBuyCount());
        courseResponse.setDescription(course.getDescription());
        courseResponse.setPoints(course.getPoints());
        courseResponse.setPrice(course.getPrice());
        List<CourseSection> courseSections = courseSectionRepository.getCourseSectionByCourse(course);
        List<CourseSectionResponse> courseSectionResponses = new ArrayList<>();
        for (CourseSection courseSection : courseSections) {
            CourseSectionResponse courseSectionResponse = new CourseSectionResponse();
            courseSectionResponse.setSectionCode(courseSection.getSectionCode());
            courseSectionResponse.setSectionName(courseSection.getSectionName());
            List<SectionCurriculumItem> sectionCurriculumItems = sectionCurriculumItemRepository.getSectionCurriculumItemByCourseSection(courseSection);
            List<CurriculumItemResponse> curriculumItemResponses = new ArrayList<>();
            for (SectionCurriculumItem sectionCurriculumItem : sectionCurriculumItems) {
                CurriculumItemResponse curriculumItemResponse = new CurriculumItemResponse();
                curriculumItemResponse.setTitle(sectionCurriculumItem.getTitle());
                curriculumItemResponse.setDescription(sectionCurriculumItem.getDescription());
                curriculumItemResponse.setCurriculumItemType(sectionCurriculumItem.getCurriculumItemType());
                curriculumItemResponses.add(curriculumItemResponse);
            }
            courseSectionResponse.setCurriculumItems(curriculumItemResponses);
            courseSectionResponses.add(courseSectionResponse);
        }
        courseResponse.setCourseSections(courseSectionResponses);
        return courseResponse;
    }

    @Override
    public SuccessResponse updateCourseByCode(CourseRequest courseRequest) {
        authentication();
        final String courseCode = courseRequest.getCode();
        final String courseTitle = courseRequest.getTitle();
        final MultipartFile image = courseRequest.getImg();
        final String video = courseRequest.getPromotionalVideo();
        final Double price = courseRequest.getPrice();
        final String description = courseRequest.getDescription();
        final String points = courseRequest.getPoints();

        if (courseCode == null || courseCode.isEmpty())
            throw new ErrorException("Invalid request", VarList.RSP_NO_DATA_FOUND);

        Course course = courseRepository.getCourseByCode(courseCode);

        if (course == null)
            throw new ErrorException("Invalid course code", VarList.RSP_NO_DATA_FOUND);

        boolean isChanged = false;
        if (courseTitle != null && !courseTitle.isEmpty()) {
            course.setCourseTitle(courseTitle);
            isChanged = true;
        }
        if (image != null && !image.isEmpty()) {
            if (!image.getContentType().startsWith("image/") || !image.getOriginalFilename().matches(".*\\.(jpg|jpeg|png|gif|bmp|webp)$")) {
                throw new ErrorException("Invalid image file type or extension. Only image files are allowed.", VarList.RSP_NO_DATA_FOUND);
            }
            try {
                FileUploadResponse imageUploadResponse = FileUploadUtil.saveFile(courseRequest.getImg(), "course-image");
                course.setImg(imageUploadResponse.getUrl());
                isChanged = true;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        if (video != null && !video.isEmpty()) {
            course.setPromotionalVideo(video);
            isChanged = true;
        }
        if (price != null && !price.toString().isEmpty()) {
            course.setPrice(price);
            isChanged = true;
        }
        if (description != null && !description.isEmpty()) {
            course.setDescription(description);
            isChanged = true;
        }
        if (points != null && !points.isEmpty()) {
            course.setPoints(points);
            isChanged = true;
        }

        if (!isChanged) {
            courseRepository.save(course);
        }
        SuccessResponse successResponse = new SuccessResponse();
        successResponse.setMessage("Course updated successfully");
        successResponse.setVariable(VarList.RSP_SUCCESS);
        return successResponse;
    }

    @Override
    public AddCourseSectionResponse addSection(AddSectionRequest addSectionRequest) {
        authentication();
        final String courseCode = addSectionRequest.getCourseCode();
        final String sectionName = addSectionRequest.getSectionName();
        if (courseCode == null || courseCode.isEmpty() || sectionName == null || sectionName.isEmpty())
            throw new ErrorException("Invalid request", VarList.RSP_NO_DATA_FOUND);
        Course course = courseRepository.getCourseByCode(courseCode);
        if (course == null)
            throw new ErrorException("Invalid request", VarList.RSP_NO_DATA_FOUND);
        CourseSection courseSection = courseSectionRepository.getCourseSectionByCourseAndSectionName(course, sectionName);
        if (courseSection != null)
            throw new ErrorException("Already added", VarList.RSP_ERROR);
        courseSection = new CourseSection();
        courseSection.setCourse(course);
        courseSection.setSectionName(sectionName);
        courseSection.setSectionCode(UUID.randomUUID().toString());
        courseSectionRepository.save(courseSection);

        AddCourseSectionResponse addCourseSectionResponse = new AddCourseSectionResponse();
        addCourseSectionResponse.setMessage("Course section added successfully");
        addCourseSectionResponse.setStatusCode(VarList.RSP_SUCCESS);
        addCourseSectionResponse.setSectionCode(courseSection.getSectionCode());
        return addCourseSectionResponse;
    }

    @Override
    public AddSectionCurriculumItemResponse addSectionItem(AddSectionCurriculumItemRequest addSectionCurriculumItemRequest) {
        authentication();
        final String courseCode = addSectionCurriculumItemRequest.getCourseCode();
        final String courseSectionCode = addSectionCurriculumItemRequest.getCourseSectionCode();
        final String description = addSectionCurriculumItemRequest.getDescription();
        final String title = addSectionCurriculumItemRequest.getTitle();

        if (courseCode == null || courseCode.isEmpty() || courseSectionCode == null ||
                courseSectionCode.isEmpty() || description == null || description.isEmpty() || title == null || title.isEmpty())
            throw new ErrorException("Invalid request", VarList.RSP_NO_DATA_FOUND);
        Course course = courseRepository.getCourseByCode(courseCode);
        if (course == null)
            throw new ErrorException("Invalid course code", VarList.RSP_NO_DATA_FOUND);
        CourseSection courseSection = courseSectionRepository.getCourseSectionBySectionCode(courseSectionCode);
        if (courseSection == null)
            throw new ErrorException("Invalid course section code", VarList.RSP_NO_DATA_FOUND);
        SectionCurriculumItem sectionCurriculumItem = sectionCurriculumItemRepository.getSectionCurriculumItemByCourseSectionAndTitle(courseSection, title);
        if (sectionCurriculumItem != null)
            throw new ErrorException("Already added", VarList.RSP_NO_DATA_FOUND);

        sectionCurriculumItem = new SectionCurriculumItem();
        sectionCurriculumItem.setCode(UUID.randomUUID().toString());
        sectionCurriculumItem.setCourseSection(courseSection);
        sectionCurriculumItem.setTitle(title);
        sectionCurriculumItem.setDescription(description);
        sectionCurriculumItemRepository.save(sectionCurriculumItem);

        AddSectionCurriculumItemResponse addSectionCurriculumItemResponse = new AddSectionCurriculumItemResponse();
        addSectionCurriculumItemResponse.setMessage("Section curriculum item added successfully");
        addSectionCurriculumItemResponse.setStatusCode(VarList.RSP_SUCCESS);
        addSectionCurriculumItemResponse.setSectionItemCode(sectionCurriculumItem.getCode());
        return addSectionCurriculumItemResponse;
    }

    @Override
    public SuccessResponse addVideo(AddVideoRequest addVideoRequest) {
        authentication();
        final String courseCode = addVideoRequest.getCourseCode();
        final String curriculumItemCode = addVideoRequest.getCurriculumItemCode();
        final String generatedVideoName = addVideoRequest.getGeneratedVideoName();
        final Double videoLength = addVideoRequest.getVideoLength();
        final String originalVideoName = addVideoRequest.getOriginalVideoName();

        CurriculumItemFileType curriculumItemFileType = curriculumItemFileTypeRepository.getCurriculumItemFileTypeById(1);

        if (curriculumItemFileType == null)
            throw new ErrorException("Curriculum item file type not found", VarList.RSP_NO_DATA_FOUND);

        if (courseCode == null || courseCode.isEmpty() ||
                curriculumItemCode == null || curriculumItemCode.isEmpty() ||
                generatedVideoName == null || generatedVideoName.isEmpty() ||
                videoLength == null || videoLength.toString().isEmpty() ||
                originalVideoName == null || originalVideoName.isEmpty())
            throw new ErrorException("Invalid request", VarList.RSP_NO_DATA_FOUND);
        Course course = courseRepository.getCourseByCode(courseCode);
        if (course == null)
            throw new ErrorException("Invalid course code", VarList.RSP_NO_DATA_FOUND);
        SectionCurriculumItem sectionCurriculumItem = sectionCurriculumItemRepository.getSectionCurriculumItemByCode(curriculumItemCode);
        if (sectionCurriculumItem == null)
            throw new ErrorException("Invalid curriculum item code", VarList.RSP_NO_DATA_FOUND);
        if (!sectionCurriculumItem.getCourseSection().getCourse().equals(course))
            throw new ErrorException("Section Curriculum Item Not applicable to course.", VarList.RSP_NO_DATA_FOUND);


        CurriculumItemFile curriculumItemFile = new CurriculumItemFile();
        curriculumItemFile.setTitle(originalVideoName);
        curriculumItemFile.setUrl(Config.VIDEOS_UPLOAD_URL + generatedVideoName);
        curriculumItemFile.setVideoLength(videoLength);
        curriculumItemFile.setSectionCurriculumItem(sectionCurriculumItem);
        curriculumItemFile.setCurriculumItemFileTypes(curriculumItemFileType);
        curriculumItemFileRepository.save(curriculumItemFile);

        SuccessResponse successResponse = new SuccessResponse();
        successResponse.setMessage("Lecture video added successfully");
        successResponse.setVariable(VarList.RSP_SUCCESS);
        return successResponse;
    }

    @Override
    public CourseSectionResponse getCurriculumItemsBySectionCode(String sectionCode) {
        authentication();
        CourseSection courseSection = courseSectionRepository.getCourseSectionBySectionCode(sectionCode);
        if (courseSection == null)
            throw new ErrorException("Invalid section code", VarList.RSP_NO_DATA_FOUND);
        List<SectionCurriculumItem> sectionCurriculumItems = sectionCurriculumItemRepository.getSectionCurriculumItemByCourseSection(courseSection);
        List<CurriculumItemResponse> curriculumItemResponses = new ArrayList<>();
        for (SectionCurriculumItem sectionCurriculumItem : sectionCurriculumItems) {
            CurriculumItemResponse curriculumItemResponse = new CurriculumItemResponse();
            curriculumItemResponse.setItemCode(sectionCurriculumItem.getCode());
            curriculumItemResponse.setDescription(sectionCurriculumItem.getDescription());
            curriculumItemResponse.setTitle(sectionCurriculumItem.getTitle());
            curriculumItemResponse.setCurriculumItemType(sectionCurriculumItem.getCurriculumItemType());
            List<CurriculumItemFileResponse> curriculumItemFileResponses = new ArrayList<>();
            List<CurriculumItemFile> curriculumItemFileList = curriculumItemFileRepository.getCurriculumItemFileBySectionCurriculumItem(sectionCurriculumItem);
            for (CurriculumItemFile curriculumItemFile : curriculumItemFileList) {
                CurriculumItemFileResponse curriculumItemFileResponse = new CurriculumItemFileResponse();
                curriculumItemFileResponse.setTitle(curriculumItemFile.getTitle());
                curriculumItemFileResponse.setUrl(curriculumItemFile.getUrl());
                curriculumItemFileResponse.setVideoLength(curriculumItemFile.getVideoLength());
                curriculumItemFileResponse.setCurriculumItemFileType(curriculumItemFile.getCurriculumItemFileTypes());
                curriculumItemFileResponses.add(curriculumItemFileResponse);
            }
            curriculumItemResponse.setCurriculumItemFiles(curriculumItemFileResponses);
            curriculumItemResponses.add(curriculumItemResponse);

            Quize quizeBySectionCurriculumItemId = quizeRepository.getQuizeBySectionCurriculumItemId(sectionCurriculumItem.getId());
            if (quizeBySectionCurriculumItemId == null) {
                curriculumItemResponse.setIsQuizeAvailable(false);
            } else {
                curriculumItemResponse.setIsQuizeAvailable(true);
            }

        }


        CourseSectionResponse courseSectionResponse = new CourseSectionResponse();
        courseSectionResponse.setCurriculumItems(curriculumItemResponses);
        courseSectionResponse.setSectionCode(courseSection.getSectionCode());
        courseSectionResponse.setSectionName(courseSection.getSectionName());
        return courseSectionResponse;
    }

    @Override
    public List<CourseSectionResponse> getCourseSectionsByCourseCode(String courseCode) {
        authentication();
        Course course = courseRepository.getCourseByCode(courseCode);
        if (course == null)
            throw new ErrorException("Invalid course code", VarList.RSP_NO_DATA_FOUND);
        List<CourseSectionResponse> courseSectionResponses = new ArrayList<>();
        List<CourseSection> courseSections = courseSectionRepository.getCourseSectionByCourse(course);
        for (CourseSection courseSection : courseSections) {
            CourseSectionResponse courseSectionResponse = new CourseSectionResponse();
            courseSectionResponse.setSectionCode(courseSection.getSectionCode());
            courseSectionResponse.setSectionName(courseSection.getSectionName());
            courseSectionResponses.add(courseSectionResponse);
        }
        return courseSectionResponses;

    }

    @Override
    public SuccessResponse addNewQuiz(String curriculumItemCode) {
        authentication();
        SectionCurriculumItem sectionCurriculumItemByCode = sectionCurriculumItemRepository.getSectionCurriculumItemByCode(curriculumItemCode);
        if (sectionCurriculumItemByCode == null)
            throw new ErrorException("Invalid curriculum Item Code", VarList.RSP_NO_DATA_FOUND);
        Quize quizeBySectionCurriculumItemId = quizeRepository.getQuizeBySectionCurriculumItemId(sectionCurriculumItemByCode.getId());
        if (quizeBySectionCurriculumItemId != null)
            throw new ErrorException("Already Added A MCQ Item For This Section Item", VarList.RSP_NO_DATA_FOUND);
        Quize quize = new Quize();
        quize.setSectionCurriculumItem(sectionCurriculumItemByCode);
        quizeRepository.save(quize);
        SuccessResponse successResponse = new SuccessResponse();
        successResponse.setVariable("200");
        successResponse.setMessage("MCQ Item Added Success");
        return successResponse;
    }

    @Override
    public SuccessResponse AddNewQuizeItem(AddQuizeItemRequest addQuizeItemRequest) {
        authentication();
        final String curriculumItemCode = addQuizeItemRequest.getCurriculumItemCode();
        final String question = addQuizeItemRequest.getQuestion();
        final String answer1 = addQuizeItemRequest.getAnswer1();
        final String answer2 = addQuizeItemRequest.getAnswer2();
        final String answer3 = addQuizeItemRequest.getAnswer3();
        final String answer4 = addQuizeItemRequest.getAnswer4();
        final int correctAnswer = addQuizeItemRequest.getCorrectAnswer();

        if (question == null || question.isEmpty() ||
                curriculumItemCode == null || curriculumItemCode.isEmpty() ||
                answer1 == null || answer1.isEmpty() ||
                answer2 == null || answer2.isEmpty() ||
                answer3 == null || answer3.isEmpty() ||
                answer4 == null || answer4.isEmpty() ||
                correctAnswer == 0)
            throw new ErrorException("Invalid request", VarList.RSP_NO_DATA_FOUND);
        SectionCurriculumItem sectionCurriculumItemByCode = sectionCurriculumItemRepository.getSectionCurriculumItemByCode(curriculumItemCode);
        if (sectionCurriculumItemByCode == null)
            throw new ErrorException("Invalid Curriculum Item Code", VarList.RSP_NO_DATA_FOUND);
        Quize quize = quizeRepository.getQuizeBySectionCurriculumItemId(sectionCurriculumItemByCode.getId());
        if (quize == null)
            throw new ErrorException("No Quiz Item Available", VarList.RSP_NO_DATA_FOUND);

        QuizItems quizItems = new QuizItems();
        quizItems.setQuestion(question);
        quizItems.setQuize(quize);
        quizItems.setCode(UUID.randomUUID().toString());

        ArrayList<Answers> answersList = new ArrayList<>();
        Answers answers1 = new Answers();
        answers1.setAnswer(answer1);
        if (correctAnswer == 1) {
            answers1.setCorrect(true);
        } else {
            answers1.setCorrect(false);
        }
        answers1.setQuizItems(quizItems);
        answersList.add(answers1);


        Answers answers2 = new Answers();
        answers2.setAnswer(answer2);
        if (correctAnswer == 2) {
            answers2.setCorrect(true);
        } else {
            answers2.setCorrect(false);
        }
        answers2.setQuizItems(quizItems);
        answersList.add(answers2);
        Answers answers3 = new Answers();
        answers3.setAnswer(answer3);
        if (correctAnswer == 3) {
            answers3.setCorrect(true);
        } else {
            answers3.setCorrect(false);
        }
        answers3.setQuizItems(quizItems);
        answersList.add(answers3);
        Answers answers4 = new Answers();
        answers4.setAnswer(answer4);
        if (correctAnswer == 4) {
            answers4.setCorrect(true);
        } else {
            answers4.setCorrect(false);
        }
        answers4.setQuizItems(quizItems);
        answersList.add(answers4);
        quizItems.setAnswers(answersList);

        quizeItemRepository.save(quizItems);

        SuccessResponse successResponse = new SuccessResponse();
        successResponse.setVariable("200");
        successResponse.setMessage("New MCQ added success");
        return successResponse;
    }

    @Override
    public QuizesInCurriculumItemResponse getQuizesByCurriculumItemCode(String curiyculumCode) {
        authentication();
        SectionCurriculumItem sectionCurriculumItemByCode = sectionCurriculumItemRepository.getSectionCurriculumItemByCode(curiyculumCode);
        if (sectionCurriculumItemByCode == null)
            throw new ErrorException("Invalid curriculum Item Code", VarList.RSP_NO_DATA_FOUND);

        Quize quizeBySectionCurriculumItemId = quizeRepository.getQuizeBySectionCurriculumItemId(sectionCurriculumItemByCode.getId());
        if (quizeBySectionCurriculumItemId == null)
            throw new ErrorException("Invalid course code", VarList.RSP_NO_DATA_FOUND);

        List<QuizItems> allByQuizeId = quizeItemRepository.getAllByQuize(quizeBySectionCurriculumItemId);

        QuizesInCurriculumItemResponse quizesInCurriculumItemResponse = new QuizesInCurriculumItemResponse();
        quizesInCurriculumItemResponse.setCurriculumItemCode(sectionCurriculumItemByCode.getCode());
        ArrayList<QuestionAndAnswerResponse> questionAndAnswerResponses = new ArrayList<>();

        for (QuizItems quizItems:allByQuizeId){
            QuestionAndAnswerResponse questionAndAnswerResponse = new QuestionAndAnswerResponse();
            questionAndAnswerResponse.setQuestionItemCode(quizItems.getCode());
            questionAndAnswerResponse.setQuestion(quizItems.getQuestion());
            List<Answers> allByQuizItems = answerRepository.getAllByQuizItems(quizItems);
            ArrayList<AnswerResponse> answerResponses=new ArrayList<>();
            for (Answers answers:allByQuizItems){
                AnswerResponse answerResponse = new AnswerResponse();
                answerResponse.setAnswer(answers.getAnswer());
                answerResponse.setIstrue(answers.isCorrect());
                answerResponses.add(answerResponse);
            }
            questionAndAnswerResponse.setAnswerResponses(answerResponses);
            questionAndAnswerResponses.add(questionAndAnswerResponse);
        }quizesInCurriculumItemResponse.setAnswerResponses(questionAndAnswerResponses);


        return quizesInCurriculumItemResponse;
    }
}
