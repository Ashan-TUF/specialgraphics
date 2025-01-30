package uk.specialgraphics.api.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
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
    private final StudentHasCourseRepository studentHasCourseRepository;
    private final CurriculumItemZipFileRepository curriculumItemZipFileRepository;
    private final UserQuizeRepository userQuizeRepository;
    private final UserAnswerRepository userAnswerRepository;
    private final UserFileRepository userFileRepository;
    private final GeneralUserProfileRepository generalUserProfileRepository;


    @Autowired
    public CourseServiceImpl(UserProfileService userProfileService, CourseRepository courseRepository, CourseSectionRepository courseSectionRepository, SectionCurriculumItemRepository sectionCurriculumItemRepository
            , CurriculumItemFileTypeRepository curriculumItemFileTypeRepository, CurriculumItemFileRepository curriculumItemFileRepository
            , QuizeRepository quizeRepository, QuizeItemRepository quizeItemRepository, AnswerRepository answerRepository
            , StudentHasCourseRepository studentHasCourseRepository, CurriculumItemZipFileRepository curriculumItemZipFileRepository
            , UserQuizeRepository userQuizeRepository, UserAnswerRepository userAnswerRepository, UserFileRepository userFileRepository, GeneralUserProfileRepository generalUserProfileRepository) {
        this.userProfileService = userProfileService;
        this.courseRepository = courseRepository;
        this.courseSectionRepository = courseSectionRepository;
        this.sectionCurriculumItemRepository = sectionCurriculumItemRepository;
        this.curriculumItemFileTypeRepository = curriculumItemFileTypeRepository;
        this.curriculumItemFileRepository = curriculumItemFileRepository;
        this.quizeRepository = quizeRepository;
        this.quizeItemRepository = quizeItemRepository;
        this.answerRepository = answerRepository;
        this.studentHasCourseRepository = studentHasCourseRepository;
        this.curriculumItemZipFileRepository = curriculumItemZipFileRepository;
        this.userQuizeRepository = userQuizeRepository;
        this.userAnswerRepository = userAnswerRepository;
        this.userFileRepository = userFileRepository;
        this.generalUserProfileRepository = generalUserProfileRepository;
    }


    private void authentication() {
        Authentication authentication;
        String username;
        GeneralUserProfile profile;
        authentication = SecurityContextHolder.getContext().getAuthentication();
        username = authentication.getName();
        profile = userProfileService.getProfile(username);

        if (profile == null) throw new ErrorException("User not found", VarList.RSP_NO_DATA_FOUND);

        if (profile.getIsActive() != 1) throw new ErrorException("User not active", VarList.RSP_NO_DATA_FOUND);

        if (profile.getGupType().getId() != 1)
            throw new ErrorException("You are not a instructor to this operation", VarList.RSP_NO_DATA_FOUND);
    }


    private GeneralUserProfile userAuthentication() {
        Authentication authentication;
        String username;
        GeneralUserProfile profile;
        authentication = SecurityContextHolder.getContext().getAuthentication();
        username = authentication.getName();
        profile = userProfileService.getProfile(username);

        if (profile == null) throw new ErrorException("User not found", VarList.RSP_NO_DATA_FOUND);

        if (profile.getIsActive() != 1) throw new ErrorException("User not active", VarList.RSP_NO_DATA_FOUND);

        if (profile.getGupType().getId() != 2)
            throw new ErrorException("You are not a Student to this operation", VarList.RSP_NO_DATA_FOUND);
        return profile;
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
        authentication();
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

        courseResponse.setPrefix(course.getSubCourseTitle());
        courseResponse.setPrefixColor(course.getSubCourseTitleColorCode());
        courseResponse.setTitleColor(course.getCourseTitleColorCode());

        courseResponse.setKeyHighlights(course.getKeyHighlights());
        courseResponse.setOutcomes(course.getProgramOutcomes());
        courseResponse.setSOutcomes(course.getSpecificprogramOutcomes());

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

        final String prefix = courseRequest.getPrefix();
        final String prefixColor = courseRequest.getPrefixColor();
        final String titleColor = courseRequest.getTitleColor();
        final String keyHighlights = courseRequest.getKeyHighlights();
        final String outcomes = courseRequest.getOutcomes();
        final String sOutcomes = courseRequest.getSOutcomes();

        if (courseCode == null || courseCode.isEmpty())
            throw new ErrorException("Invalid request", VarList.RSP_NO_DATA_FOUND);

        Course course = courseRepository.getCourseByCode(courseCode);

        if (course == null) throw new ErrorException("Invalid course code", VarList.RSP_NO_DATA_FOUND);

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
        if (prefix != null && !prefix.isEmpty()) {
            course.setSubCourseTitle(prefix);
            isChanged = true;
        }
        if (prefixColor != null && !prefixColor.isEmpty()) {
            course.setSubCourseTitleColorCode(prefixColor);
            isChanged = true;
        }
        if (titleColor != null && !titleColor.isEmpty()) {
            course.setCourseTitleColorCode(titleColor);
            isChanged = true;
        }
        if (keyHighlights != null && !keyHighlights.isEmpty()) {
            course.setKeyHighlights(keyHighlights);
            isChanged = true;
        }
        if (outcomes != null && !outcomes.isEmpty()) {
            course.setProgramOutcomes(outcomes);
            isChanged = true;
        }
        if (sOutcomes != null && !sOutcomes.isEmpty()) {
            course.setSpecificprogramOutcomes(sOutcomes);
            isChanged = true;
        }

        if (isChanged) {
            courseRepository.save(course);
        } else {
            throw new ErrorException("No changed components", VarList.RSP_NO_DATA_FOUND);

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
        if (course == null) throw new ErrorException("Invalid request", VarList.RSP_NO_DATA_FOUND);
        CourseSection courseSection = courseSectionRepository.getCourseSectionByCourseAndSectionName(course, sectionName);
        if (courseSection != null) throw new ErrorException("Already added", VarList.RSP_ERROR);
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

        if (courseCode == null || courseCode.isEmpty() || courseSectionCode == null || courseSectionCode.isEmpty() || description == null || description.isEmpty() || title == null || title.isEmpty())
            throw new ErrorException("Invalid request", VarList.RSP_NO_DATA_FOUND);
        Course course = courseRepository.getCourseByCode(courseCode);
        if (course == null) throw new ErrorException("Invalid course code", VarList.RSP_NO_DATA_FOUND);
        CourseSection courseSection = courseSectionRepository.getCourseSectionBySectionCode(courseSectionCode);
        if (courseSection == null) throw new ErrorException("Invalid course section code", VarList.RSP_NO_DATA_FOUND);
        SectionCurriculumItem sectionCurriculumItem = sectionCurriculumItemRepository.getSectionCurriculumItemByCourseSectionAndTitle(courseSection, title);
        if (sectionCurriculumItem != null) throw new ErrorException("Already added", VarList.RSP_NO_DATA_FOUND);

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

        if (courseCode == null || courseCode.isEmpty() || curriculumItemCode == null || curriculumItemCode.isEmpty() || generatedVideoName == null || generatedVideoName.isEmpty() || videoLength == null || videoLength.toString().isEmpty() || originalVideoName == null || originalVideoName.isEmpty())
            throw new ErrorException("Invalid request", VarList.RSP_NO_DATA_FOUND);
        Course course = courseRepository.getCourseByCode(courseCode);
        if (course == null) throw new ErrorException("Invalid course code", VarList.RSP_NO_DATA_FOUND);
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
        if (courseSection == null) throw new ErrorException("Invalid section code", VarList.RSP_NO_DATA_FOUND);
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
                curriculumItemFileResponse.setId(curriculumItemFile.getId());
                curriculumItemFileResponse.setVideoLength(curriculumItemFile.getVideoLength());
                curriculumItemFileResponse.setCurriculumItemFileType(curriculumItemFile.getCurriculumItemFileTypes());
                curriculumItemFileResponses.add(curriculumItemFileResponse);
            }
            curriculumItemResponse.setCurriculumItemFiles(curriculumItemFileResponses);

            List<CurriculumItemZipFile> curriculumItemFileBySectionCurriculumItem = curriculumItemZipFileRepository.getCurriculumItemFileBySectionCurriculumItem(sectionCurriculumItem);
            List<CurriculumItemZipFileResponse> curriculumItemZipFileResponses = new ArrayList<>();
            for (CurriculumItemZipFile curriculumItemZipFile : curriculumItemFileBySectionCurriculumItem) {
                CurriculumItemZipFileResponse curriculumItemZipFileResponse = new CurriculumItemZipFileResponse();
                curriculumItemZipFileResponse.setId(curriculumItemZipFile.getId());
                curriculumItemZipFileResponse.setTitle(curriculumItemZipFile.getTitle());
                curriculumItemZipFileResponse.setUrl(curriculumItemZipFile.getUrl());
                curriculumItemZipFileResponses.add(curriculumItemZipFileResponse);
            }
            curriculumItemResponse.setCurriculumItemZipFileResponses(curriculumItemZipFileResponses);
            curriculumItemResponses.add(curriculumItemResponse);
            Quiz quizBySectionCurriculumItemId = quizeRepository.getQuizBySectionCurriculumItemId(sectionCurriculumItem.getId());
            if (quizBySectionCurriculumItemId == null) {
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
        if (course == null) throw new ErrorException("Invalid course code", VarList.RSP_NO_DATA_FOUND);
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
            throw new ErrorException("Invalid curriculum item code", VarList.RSP_NO_DATA_FOUND);
        Quiz quizBySectionCurriculumItemId = quizeRepository.getQuizBySectionCurriculumItemId(sectionCurriculumItemByCode.getId());
        if (quizBySectionCurriculumItemId != null)
            throw new ErrorException("Already added a MCQ item for this section item", VarList.RSP_NO_DATA_FOUND);
        Quiz quiz = new Quiz();
        quiz.setSectionCurriculumItem(sectionCurriculumItemByCode);
        quizeRepository.save(quiz);
        SuccessResponse successResponse = new SuccessResponse();
        successResponse.setVariable(VarList.RSP_SUCCESS);
        successResponse.setMessage("MCQ item added success");
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

        if (question == null || question.isEmpty() || curriculumItemCode == null || curriculumItemCode.isEmpty() || answer1 == null || answer1.isEmpty() || answer2 == null || answer2.isEmpty() || answer3 == null || answer3.isEmpty() || answer4 == null || answer4.isEmpty() || correctAnswer == 0)
            throw new ErrorException("Invalid request", VarList.RSP_NO_DATA_FOUND);
        SectionCurriculumItem sectionCurriculumItemByCode = sectionCurriculumItemRepository.getSectionCurriculumItemByCode(curriculumItemCode);
        if (sectionCurriculumItemByCode == null)
            throw new ErrorException("Invalid Curriculum Item Code", VarList.RSP_NO_DATA_FOUND);
        Quiz quiz = quizeRepository.getQuizBySectionCurriculumItemId(sectionCurriculumItemByCode.getId());
        if (quiz == null) throw new ErrorException("No Quiz Item Available", VarList.RSP_NO_DATA_FOUND);

        QuizItems quizItems = new QuizItems();
        quizItems.setQuestion(question);
        quizItems.setQuiz(quiz);
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

        Quiz quizBySectionCurriculumItemId = quizeRepository.getQuizBySectionCurriculumItemId(sectionCurriculumItemByCode.getId());
        if (quizBySectionCurriculumItemId == null)
            throw new ErrorException("Invalid course code", VarList.RSP_NO_DATA_FOUND);

        List<QuizItems> allByQuizeId = quizeItemRepository.getAllByQuiz(quizBySectionCurriculumItemId);

        QuizesInCurriculumItemResponse quizesInCurriculumItemResponse = new QuizesInCurriculumItemResponse();
        quizesInCurriculumItemResponse.setCurriculumItemCode(sectionCurriculumItemByCode.getCode());
        ArrayList<QuestionAndAnswerResponse> questionAndAnswerResponses = new ArrayList<>();

        for (QuizItems quizItems : allByQuizeId) {
            QuestionAndAnswerResponse questionAndAnswerResponse = new QuestionAndAnswerResponse();
            questionAndAnswerResponse.setQuestionItemCode(quizItems.getCode());
            questionAndAnswerResponse.setQuestion(quizItems.getQuestion());
            List<Answers> allByQuizItems = answerRepository.getAllByQuizItems(quizItems);
            ArrayList<AnswerResponse> answerResponses = new ArrayList<>();
            for (Answers answers : allByQuizItems) {
                AnswerResponse answerResponse = new AnswerResponse();
                answerResponse.setAnswer(answers.getAnswer());
                answerResponse.setIstrue(answers.isCorrect());
                answerResponses.add(answerResponse);
            }
            questionAndAnswerResponse.setAnswerResponses(answerResponses);
            questionAndAnswerResponses.add(questionAndAnswerResponse);
        }
        quizesInCurriculumItemResponse.setAnswerResponses(questionAndAnswerResponses);

        return quizesInCurriculumItemResponse;
    }

    @Override
    public SuccessResponse updateNewQuizeItem(UpdateQuizeItemRequest updateQuizeItemRequest) {
        authentication();
        final String questionItemCode = updateQuizeItemRequest.getQuestionItemCode();
        final String question = updateQuizeItemRequest.getQuestion();
        final String answer1 = updateQuizeItemRequest.getAnswer1();
        final String answer2 = updateQuizeItemRequest.getAnswer2();
        final String answer3 = updateQuizeItemRequest.getAnswer3();
        final String answer4 = updateQuizeItemRequest.getAnswer4();
        final int correctAnswer = updateQuizeItemRequest.getCorrectAnswer();

        if (question == null || question.isEmpty() || questionItemCode == null || questionItemCode.isEmpty() || answer1 == null || answer1.isEmpty() || answer2 == null || answer2.isEmpty() || answer3 == null || answer3.isEmpty() || answer4 == null || answer4.isEmpty() || correctAnswer == 0)
            throw new ErrorException("Invalid request", VarList.RSP_NO_DATA_FOUND);

        QuizItems quizItemsByCode = quizeItemRepository.getQuizItemsByCode(questionItemCode);
        if (quizItemsByCode == null) throw new ErrorException("Invalid Quiz Item ", VarList.RSP_NO_DATA_FOUND);

        int answerNumber = 1;
        List<Answers> allByQuizItems = answerRepository.getAllByQuizItems(quizItemsByCode);
        if (allByQuizItems != null) {
            for (Answers answer : allByQuizItems) {
                if (answerNumber == 1) {
                    answer.setAnswer(answer1);
                    if (correctAnswer == 1) {
                        answer.setCorrect(true);
                    } else {
                        answer.setCorrect(false);
                    }
                    answerRepository.save(answer);

                } else if (answerNumber == 2) {
                    if (correctAnswer == 2) {
                        answer.setCorrect(true);
                    } else {
                        answer.setCorrect(false);
                    }

                    answer.setAnswer(answer2);
                    answerRepository.save(answer);
                } else if (answerNumber == 3) {
                    if (correctAnswer == 3) {
                        answer.setCorrect(true);
                    } else {
                        answer.setCorrect(false);
                    }

                    answer.setAnswer(answer3);
                    answerRepository.save(answer);
                } else {
                    if (correctAnswer == 4) {
                        answer.setCorrect(true);
                    } else {
                        answer.setCorrect(false);
                    }
                    answer.setAnswer(answer4);
                    answerRepository.save(answer);

                }
                if (answerNumber == 4) {
                    break;
                }
                answerNumber++;

            }
        }

        quizItemsByCode.setQuestion(question);
        quizeItemRepository.save(quizItemsByCode);

        SuccessResponse successResponse = new SuccessResponse();
        successResponse.setVariable("200");
        successResponse.setMessage("Update success");
        return successResponse;

    }

    @Override
    public GetCourseDetailsByCourseCodeResponse getCourseDetailsByCourseCode(String courseCode) {
        Course course = courseRepository.getCourseByCode(courseCode);

        if (course == null)
            throw new ErrorException("Invalid course code", VarList.RSP_NO_DATA_FOUND);

        GetCourseDetailsByCourseCodeResponse getCourseDetailsByCourseCodeResponse = new GetCourseDetailsByCourseCodeResponse();
        getCourseDetailsByCourseCodeResponse.setCode(course.getCode());
        getCourseDetailsByCourseCodeResponse.setTitle(course.getCourseTitle());
        getCourseDetailsByCourseCodeResponse.setImg(course.getImg());
        getCourseDetailsByCourseCodeResponse.setPromotionalVideo(course.getPromotionalVideo());
        getCourseDetailsByCourseCodeResponse.setCreatedDate(course.getCreatedDate());
        getCourseDetailsByCourseCodeResponse.setBuyCount(course.getBuyCount());
        getCourseDetailsByCourseCodeResponse.setDescription(course.getDescription());
        getCourseDetailsByCourseCodeResponse.setPoints(course.getPoints());
        getCourseDetailsByCourseCodeResponse.setPrice(course.getPrice());

        getCourseDetailsByCourseCodeResponse.setPrefix(course.getSubCourseTitle());
        getCourseDetailsByCourseCodeResponse.setPrefixColor(course.getSubCourseTitleColorCode());
        getCourseDetailsByCourseCodeResponse.setTitleColor(course.getCourseTitleColorCode());

        getCourseDetailsByCourseCodeResponse.setKeyHighlights(course.getKeyHighlights());
        getCourseDetailsByCourseCodeResponse.setOutcomes(course.getProgramOutcomes());
        getCourseDetailsByCourseCodeResponse.setSOutcomes(course.getSpecificprogramOutcomes());

        List<GetCourseSectionResponse> courseSections = new ArrayList<>();
        List<CourseSection> courseSectionList = courseSectionRepository.getCourseSectionByCourse(course);
        for (CourseSection courseSection : courseSectionList) {
            GetCourseSectionResponse courseSectionResponse = new GetCourseSectionResponse();
            courseSectionResponse.setSectionName(courseSection.getSectionName());
            List<GetCurriculumItemResponse> curriculumItems = new ArrayList<>();
            List<SectionCurriculumItem> sectionCurriculumItems = sectionCurriculumItemRepository.getSectionCurriculumItemByCourseSection(courseSection);
            for (SectionCurriculumItem sectionCurriculumItem : sectionCurriculumItems) {
                GetCurriculumItemResponse curriculumItemResponse = new GetCurriculumItemResponse();
                curriculumItemResponse.setDescription(sectionCurriculumItem.getDescription());
                curriculumItemResponse.setTitle(sectionCurriculumItem.getTitle());
                curriculumItemResponse.setCurriculumItemType(sectionCurriculumItem.getCurriculumItemType());
                curriculumItems.add(curriculumItemResponse);
            }
            courseSectionResponse.setCurriculumItems(curriculumItems);
            courseSections.add(courseSectionResponse);
        }
        getCourseDetailsByCourseCodeResponse.setCourseSections(courseSections);
        return getCourseDetailsByCourseCodeResponse;
    }

    @Override
    public List<UserCourseResponse> getAllUserCourses() {
        GeneralUserProfile generalUserProfile = userAuthentication();
        List<StudentHasCourse> allByGeneralUserProfile = studentHasCourseRepository.getAllByGeneralUserProfile(generalUserProfile);
        if (allByGeneralUserProfile == null)
            throw new ErrorException("No Purchased Course", VarList.RSP_NO_DATA_FOUND);

        ArrayList<UserCourseResponse> userCourseResponses = new ArrayList<>();
        for (StudentHasCourse studentHasCourse : allByGeneralUserProfile) {
            Course course = studentHasCourse.getCourse();
            UserCourseResponse userCourseResponse = new UserCourseResponse();
            userCourseResponse.setCourseCode(course.getCode());
            userCourseResponse.setTitle(course.getCourseTitle());
            userCourseResponse.setImage(course.getImg());
            userCourseResponse.setProgress(studentHasCourse.getProgress());
            userCourseResponses.add(userCourseResponse);
        }
        return userCourseResponses;
    }

    @Override
    public UserCourseViewResponse getUserCourseDetailsByCourseCode(String courseCode) {
        GeneralUserProfile generalUserProfile = userAuthentication();
        Course course = courseRepository.getCourseByCode(courseCode);
        StudentHasCourse studentCourse = studentHasCourseRepository.getStudentHasCourseByCourseAndGeneralUserProfile(course, generalUserProfile);
        if (studentCourse == null)
            throw new ErrorException("Invalid course Request", VarList.RSP_NO_DATA_FOUND);

        UserCourseViewResponse userCourseViewResponse = new UserCourseViewResponse();

        userCourseViewResponse.setCode(course.getCode());
        userCourseViewResponse.setTitle(course.getCourseTitle());
        userCourseViewResponse.setImg(course.getImg());
        userCourseViewResponse.setDescription(course.getDescription());
        userCourseViewResponse.setPoints(course.getPoints());

        boolean breakTheLoop = false;
        boolean breakCurriItem = false;
        ArrayList<UserCourseSectionResponse> userCourseSectionResponses = new ArrayList<>();
        List<CourseSection> courseSectionList = courseSectionRepository.getCourseSectionByCourse(studentCourse.getCourse());
        for (CourseSection courseSection : courseSectionList) {
            if (breakTheLoop) {
                userCourseViewResponse.setNextLesson(courseSection.getSectionName());
                break;
            }
            UserCourseSectionResponse courseSectionResponse = new UserCourseSectionResponse();
            courseSectionResponse.setSectionName(courseSection.getSectionName());
            courseSectionResponse.setSectionCode(courseSection.getSectionCode());
            List<UserCurriculumItemResponse> curriculumItems = new ArrayList<>();
            List<SectionCurriculumItem> sectionCurriculumItems = sectionCurriculumItemRepository.getSectionCurriculumItemByCourseSection(courseSection);
            for (int i = 0; i < sectionCurriculumItems.size(); i++) {

                if (breakCurriItem) {
                    break;
                }
                SectionCurriculumItem sectionCurriculumItem = sectionCurriculumItems.get(i);
                UserCurriculumItemResponse curriculumItemResponse = new UserCurriculumItemResponse();
                curriculumItemResponse.setDescription(sectionCurriculumItem.getDescription());
                curriculumItemResponse.setTitle(sectionCurriculumItem.getTitle());
                curriculumItemResponse.setCurriculumItemType(sectionCurriculumItem.getCurriculumItemType());
                curriculumItemResponse.setItemCode(sectionCurriculumItem.getCode());
                List<CurriculumItemFile> curriculumItemFileBySectionCurriculumItem = curriculumItemFileRepository.getCurriculumItemFileBySectionCurriculumItem(sectionCurriculumItem);
                List<UserCurriculumItemFileResponse> curriculumItemFileResponses = new ArrayList<>();
                for (CurriculumItemFile curriculumItemFile : curriculumItemFileBySectionCurriculumItem) {
                    UserCurriculumItemFileResponse userCurriculumItemFileResponse = new UserCurriculumItemFileResponse();
                    userCurriculumItemFileResponse.setTitle(curriculumItemFile.getTitle());
                    userCurriculumItemFileResponse.setUrl(curriculumItemFile.getUrl());
                    userCurriculumItemFileResponse.setVideoLength(curriculumItemFile.getVideoLength());
                    curriculumItemFileResponses.add(userCurriculumItemFileResponse);

                }
                List<CurriculumItemZipFile> curriculumzipFileBySectionCurriculumItem = curriculumItemZipFileRepository.getCurriculumItemFileBySectionCurriculumItem(sectionCurriculumItem);
                List<CurriculumItemZipFileResponse> curriculumItemZipFileResponses = new ArrayList<>();

                for (CurriculumItemZipFile curriculumItemZipFile : curriculumzipFileBySectionCurriculumItem) {
                    CurriculumItemZipFileResponse curriculumItemZipFileResponse = new CurriculumItemZipFileResponse();

                    curriculumItemZipFileResponse.setTitle(curriculumItemZipFile.getTitle());
                    UserZipFile userZipFileByCurriculumItemZipFile = userFileRepository.getUserZipFileByCurriculumItemZipFile(curriculumItemZipFile);
                    if (userZipFileByCurriculumItemZipFile != null) {
                        curriculumItemZipFileResponse.setComplete(true);
                        if (userZipFileByCurriculumItemZipFile.getMarks() == 0) {
                            breakTheLoop = true;
                            breakCurriItem = true;
                        }
                        curriculumItemZipFileResponse.setMarks(userZipFileByCurriculumItemZipFile.getMarks());

                    } else {
                        breakTheLoop = true;
                        breakCurriItem = true;
                        curriculumItemZipFileResponse.setId(curriculumItemZipFile.getId());
                        curriculumItemZipFileResponse.setUrl(curriculumItemZipFile.getUrl());
                    }
                    curriculumItemZipFileResponses.add(curriculumItemZipFileResponse);

                }
                curriculumItemResponse.setCurriculumItemZipFileResponse(curriculumItemZipFileResponses);
                curriculumItemResponse.setCurriculumItemFiles(curriculumItemFileResponses);
                Quiz quizBySectionCurriculumItemId = quizeRepository.getQuizBySectionCurriculumItemId(sectionCurriculumItem.getId());
                if (quizBySectionCurriculumItemId == null) {
                    curriculumItemResponse.setIsQuizeAvailable(false);
                    curriculumItemResponse.setIsQuizPerform(false);
                } else {
                    curriculumItemResponse.setIsQuizeAvailable(true);
                    UserQuiz userQuizByQuizSectionCurriculumItem = userQuizeRepository.getUserQuizByQuizSectionCurriculumItem(sectionCurriculumItem);
                    if (userQuizByQuizSectionCurriculumItem == null) {
                        curriculumItemResponse.setIsQuizPerform(false);
                        breakTheLoop = true;
                        breakCurriItem = true;
                    } else {
                        curriculumItemResponse.setIsQuizPerform(true);

                    }
                }
                curriculumItems.add(curriculumItemResponse);

            }
            courseSectionResponse.setCurriculumItems(curriculumItems);
            userCourseSectionResponses.add(courseSectionResponse);
        }
        userCourseViewResponse.setCourseSections(userCourseSectionResponses);
//        return getCourseDetailsByCourseCodeResponse;
        return userCourseViewResponse;
    }

    @Override
    public List<UserCourseViewResponse> getAdminViewUserCourseDetailsByCourseCode(String email) {

        authentication();
        GeneralUserProfile generalUserProfileByEmail = generalUserProfileRepository.getGeneralUserProfileByEmail(email);
        if (generalUserProfileByEmail == null) {
            throw new ErrorException("Invalid User Email", VarList.RSP_NO_DATA_FOUND);
        }

        List<StudentHasCourse> studentCourse = studentHasCourseRepository.getAllByGeneralUserProfile(generalUserProfileByEmail);
        List<UserCourseViewResponse> userCourseViewResponses = new ArrayList<>();
        for (StudentHasCourse studentHasCourse : studentCourse) {
            if (studentCourse == null)
                throw new ErrorException("Invalid course Request", VarList.RSP_NO_DATA_FOUND);
            Course course = studentHasCourse.getCourse();
            UserCourseViewResponse userCourseViewResponse = new UserCourseViewResponse();

            userCourseViewResponse.setCode(course.getCode());
            userCourseViewResponse.setTitle(course.getCourseTitle());
            userCourseViewResponse.setImg(course.getImg());
            userCourseViewResponse.setDescription(course.getDescription());
            userCourseViewResponse.setPoints(course.getPoints());

            boolean breakTheLoop = false;
            ArrayList<UserCourseSectionResponse> userCourseSectionResponses = new ArrayList<>();
            List<CourseSection> courseSectionList = courseSectionRepository.getCourseSectionByCourse(course);
            for (CourseSection courseSection : courseSectionList) {
                if (breakTheLoop) {
                    userCourseViewResponse.setNextLesson(courseSection.getSectionName());
                    break;
                }
                UserCourseSectionResponse courseSectionResponse = new UserCourseSectionResponse();
                courseSectionResponse.setSectionName(courseSection.getSectionName());
                courseSectionResponse.setSectionCode(courseSection.getSectionCode());
                List<UserCurriculumItemResponse> curriculumItems = new ArrayList<>();
                List<SectionCurriculumItem> sectionCurriculumItems = sectionCurriculumItemRepository.getSectionCurriculumItemByCourseSection(courseSection);
                for (int i = 0; i < sectionCurriculumItems.size(); i++) {
                    SectionCurriculumItem sectionCurriculumItem = sectionCurriculumItems.get(i);
                    UserCurriculumItemResponse curriculumItemResponse = new UserCurriculumItemResponse();
                    curriculumItemResponse.setDescription(sectionCurriculumItem.getDescription());
                    curriculumItemResponse.setTitle(sectionCurriculumItem.getTitle());
                    curriculumItemResponse.setCurriculumItemType(sectionCurriculumItem.getCurriculumItemType());
                    curriculumItemResponse.setItemCode(sectionCurriculumItem.getCode());
                    List<CurriculumItemFile> curriculumItemFileBySectionCurriculumItem = curriculumItemFileRepository.getCurriculumItemFileBySectionCurriculumItem(sectionCurriculumItem);
                    List<UserCurriculumItemFileResponse> curriculumItemFileResponses = new ArrayList<>();
                    for (CurriculumItemFile curriculumItemFile : curriculumItemFileBySectionCurriculumItem) {
                        UserCurriculumItemFileResponse userCurriculumItemFileResponse = new UserCurriculumItemFileResponse();
                        userCurriculumItemFileResponse.setTitle(curriculumItemFile.getTitle());
                        userCurriculumItemFileResponse.setUrl(curriculumItemFile.getUrl());
                        userCurriculumItemFileResponse.setVideoLength(curriculumItemFile.getVideoLength());
                        curriculumItemFileResponses.add(userCurriculumItemFileResponse);

                    }
                    List<CurriculumItemZipFile> curriculumzipFileBySectionCurriculumItem = curriculumItemZipFileRepository.getCurriculumItemFileBySectionCurriculumItem(sectionCurriculumItem);
                    List<CurriculumItemZipFileResponse> curriculumItemZipFileResponses = new ArrayList<>();


                    for (CurriculumItemZipFile curriculumItemZipFile : curriculumzipFileBySectionCurriculumItem) {
                        CurriculumItemZipFileResponse curriculumItemZipFileResponse = new CurriculumItemZipFileResponse();

                        curriculumItemZipFileResponse.setTitle(curriculumItemZipFile.getTitle());
                        UserZipFile userZipFileByCurriculumItemZipFile = userFileRepository.getUserZipFileByCurriculumItemZipFile(curriculumItemZipFile);
                        if (userZipFileByCurriculumItemZipFile != null) {

                            curriculumItemZipFileResponse.setComplete(userZipFileByCurriculumItemZipFile.isStatus());
                            curriculumItemZipFileResponse.setId(userZipFileByCurriculumItemZipFile.getId());
                            curriculumItemZipFileResponse.setMarks(userZipFileByCurriculumItemZipFile.getMarks());
                            curriculumItemZipFileResponse.setDate(userZipFileByCurriculumItemZipFile.getUploadDate().toString());
                            curriculumItemZipFileResponse.setUrl(userZipFileByCurriculumItemZipFile.getUrl());


                        } else {
                            breakTheLoop = true;
                            curriculumItemZipFileResponse.setComplete(false);
                        }
                        curriculumItemZipFileResponses.add(curriculumItemZipFileResponse);

                    }
                    curriculumItemResponse.setCurriculumItemZipFileResponse(curriculumItemZipFileResponses);
                    curriculumItemResponse.setCurriculumItemFiles(curriculumItemFileResponses);
                    Quiz quizBySectionCurriculumItemId = quizeRepository.getQuizBySectionCurriculumItemId(sectionCurriculumItem.getId());
                    if (quizBySectionCurriculumItemId == null) {
                        curriculumItemResponse.setIsQuizeAvailable(false);
                        curriculumItemResponse.setIsQuizPerform(false);
                    } else {
                        curriculumItemResponse.setIsQuizeAvailable(true);
                        UserQuiz userQuizByQuizSectionCurriculumItem = userQuizeRepository.getUserQuizByQuizSectionCurriculumItem(sectionCurriculumItem);
                        if (userQuizByQuizSectionCurriculumItem == null) {
                            curriculumItemResponse.setIsQuizPerform(false);
                            breakTheLoop = true;
                        } else {
                            curriculumItemResponse.setIsQuizPerform(true);

                        }
                    }
                    curriculumItems.add(curriculumItemResponse);

                }
                courseSectionResponse.setCurriculumItems(curriculumItems);
                userCourseSectionResponses.add(courseSectionResponse);
            }
            userCourseViewResponse.setCourseSections(userCourseSectionResponses);
            userCourseViewResponses.add(userCourseViewResponse);

        }
        return userCourseViewResponses;
    }

    @Override
    public SuccessResponse addZip(CurriculumItemFileUploadRequest fileUploadRequest) {
        authentication();
        String title = fileUploadRequest.getTitle();
        String code = fileUploadRequest.getCurriculumCode();
        MultipartFile zipFile = fileUploadRequest.getZip();

        if (title == null || title.isEmpty() || code == null || code.isEmpty() || zipFile == null)
            throw new ErrorException("Invalid request", VarList.RSP_NO_DATA_FOUND);

        SectionCurriculumItem sectionCurriculumItemByCode = sectionCurriculumItemRepository.getSectionCurriculumItemByCode(fileUploadRequest.getCurriculumCode());
        if (sectionCurriculumItemByCode == null)
            throw new ErrorException("Invalid curriculum Request", VarList.RSP_NO_DATA_FOUND);

        CurriculumItemZipFile curriculumItemZipFile = new CurriculumItemZipFile();
        curriculumItemZipFile.setTitle(title);
        curriculumItemZipFile.setSectionCurriculumItem(sectionCurriculumItemByCode);

        try {
            FileUploadResponse imageUploadResponse = FileUploadUtil.saveFile(zipFile, "zip");
            curriculumItemZipFile.setUrl(imageUploadResponse.getUrl());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        curriculumItemZipFileRepository.save(curriculumItemZipFile);
        SuccessResponse successResponse = new SuccessResponse();
        successResponse.setVariable("200");
        successResponse.setMessage("File Upload Success");
        return successResponse;
    }

    @Override
    public SuccessResponse updateZip(CurriculumItemFileUpdateRequest curriculumItemFileUpdateRequest) {
        authentication();
        String title = curriculumItemFileUpdateRequest.getTitle();
        String id = curriculumItemFileUpdateRequest.getId();
        MultipartFile zipFile = curriculumItemFileUpdateRequest.getZip();

        if (title == null || title.isEmpty() || id == null || id.isEmpty())
            throw new ErrorException("Invalid request", VarList.RSP_NO_DATA_FOUND);

        CurriculumItemZipFile curriculumItemZipFileById = curriculumItemZipFileRepository.getCurriculumItemZipFileById(Integer.parseInt(id));
        if (curriculumItemZipFileById == null) {
            throw new ErrorException("Invalid Id", VarList.RSP_NO_DATA_FOUND);
        }

        boolean changeStat = false;
        if (!title.equals(curriculumItemZipFileById.getTitle())) {
            changeStat = true;
            curriculumItemZipFileById.setTitle(title);
        }

        if (zipFile != null && !zipFile.isEmpty()) {
            try {
                FileUploadResponse imageUploadResponse = FileUploadUtil.saveFile(zipFile, "zip");
                curriculumItemZipFileById.setUrl(imageUploadResponse.getUrl());
                changeStat = true;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        if (changeStat) {
            curriculumItemZipFileRepository.save(curriculumItemZipFileById);

            SuccessResponse successResponse = new SuccessResponse();
            successResponse.setVariable("200");
            successResponse.setMessage("Updated Success");
            return successResponse;
        } else {
            throw new ErrorException("Same Details", VarList.RSP_NO_DATA_FOUND);
        }


    }

    @Override
    public UserQuizesInCurriculumItemResponse getUserQuizesByCurriculumItemCode(String courseCode, String curiyculumCode) {

        GeneralUserProfile generalUserProfile = userAuthentication();
        SectionCurriculumItem sectionCurriculumItemByCode = sectionCurriculumItemRepository.getSectionCurriculumItemByCode(curiyculumCode);
        if (sectionCurriculumItemByCode == null)
            throw new ErrorException("Invalid curriculum Item Code", VarList.RSP_NO_DATA_FOUND);
        StudentHasCourse studentHasCourseByCourseAndGeneralUserProfile = studentHasCourseRepository.getStudentHasCourseByCourseAndGeneralUserProfile(sectionCurriculumItemByCode.getCourseSection().getCourse(), generalUserProfile);
        if (studentHasCourseByCourseAndGeneralUserProfile == null)
            throw new ErrorException("Course not Purchased", VarList.RSP_NO_DATA_FOUND);

        Quiz quizBySectionCurriculumItemId = quizeRepository.getQuizBySectionCurriculumItemId(sectionCurriculumItemByCode.getId());
        if (quizBySectionCurriculumItemId == null)
            throw new ErrorException("Invalid course code", VarList.RSP_NO_DATA_FOUND);

        List<QuizItems> allByQuizeId = quizeItemRepository.getAllByQuiz(quizBySectionCurriculumItemId);

        UserQuizesInCurriculumItemResponse quizesInCurriculumItemResponse = new UserQuizesInCurriculumItemResponse();
        quizesInCurriculumItemResponse.setCurriculumItemCode(sectionCurriculumItemByCode.getCode());
        ArrayList<UserQuestionAndAnswerResponse> questionAndAnswerResponses = new ArrayList<>();

        for (QuizItems quizItems : allByQuizeId) {
            UserQuestionAndAnswerResponse questionAndAnswerResponse = new UserQuestionAndAnswerResponse();
            questionAndAnswerResponse.setQuestionItemCode(quizItems.getCode());
            questionAndAnswerResponse.setQuestion(quizItems.getQuestion());
            List<Answers> allByQuizItems = answerRepository.getAllByQuizItems(quizItems);
            ArrayList<AnswerResponse> answerResponses = new ArrayList<>();
            for (Answers answers : allByQuizItems) {
                AnswerResponse answerResponse = new AnswerResponse();
                answerResponse.setAnswer(answers.getAnswer());
                answerResponse.setId(answers.getId());
                answerResponses.add(answerResponse);
            }
            questionAndAnswerResponse.setAnswerResponses(answerResponses);
            questionAndAnswerResponses.add(questionAndAnswerResponse);
        }
        quizesInCurriculumItemResponse.setAnswerResponses(questionAndAnswerResponses);

        return quizesInCurriculumItemResponse;

    }


    public UserPerformeQuizeAndAnswersResponse getUserAnswersForQuizesByCurriculumItemCode(String curiyculumCode) {

        GeneralUserProfile generalUserProfile = userAuthentication();
        SectionCurriculumItem sectionCurriculumItemByCode = sectionCurriculumItemRepository.getSectionCurriculumItemByCode(curiyculumCode);
        if (sectionCurriculumItemByCode == null)
            throw new ErrorException("Invalid curriculum Item Code", VarList.RSP_NO_DATA_FOUND);
        StudentHasCourse studentHasCourseByCourseAndGeneralUserProfile = studentHasCourseRepository.getStudentHasCourseByCourseAndGeneralUserProfile(sectionCurriculumItemByCode.getCourseSection().getCourse(), generalUserProfile);
        if (studentHasCourseByCourseAndGeneralUserProfile == null)
            throw new ErrorException("Course not Purchased", VarList.RSP_NO_DATA_FOUND);

        UserQuiz userQuizByQuizSectionCurriculumItem = userQuizeRepository.getUserQuizByQuizSectionCurriculumItem(sectionCurriculumItemByCode);
        if (userQuizByQuizSectionCurriculumItem == null)
            throw new ErrorException("Invalid Request", VarList.RSP_NO_DATA_FOUND);

        Quiz quizBySectionCurriculumItemId = quizeRepository.getQuizBySectionCurriculumItemId(sectionCurriculumItemByCode.getId());
        if (quizBySectionCurriculumItemId == null)
            throw new ErrorException("Invalid course code", VarList.RSP_NO_DATA_FOUND);


        List<QuizItems> allByQuizeId = quizeItemRepository.getAllByQuiz(userQuizByQuizSectionCurriculumItem.getQuiz());

        UserPerformeQuizeAndAnswersResponse quizesInCurriculumItemResponse = new UserPerformeQuizeAndAnswersResponse();
        ArrayList<UserPerformAnswerResponse> questionAndAnswerResponses = new ArrayList<>();
        for (QuizItems quizItems : allByQuizeId) {

            UserPerformAnswerResponse questionAndAnswerResponse = new UserPerformAnswerResponse();
            questionAndAnswerResponse.setQuestionItemCode(quizItems.getCode());
            UserAnswers userAnswersByQuizItem = userAnswerRepository.getUserAnswersByQuizItem(quizItems);
            AnswerResponse userAnswerResponse = new AnswerResponse();
            userAnswerResponse.setAnswer(userAnswersByQuizItem.getUserAnswer().getAnswer());
            userAnswerResponse.setIstrue(userAnswersByQuizItem.getUserAnswer().isCorrect());
            userAnswerResponse.setId(userAnswersByQuizItem.getUserAnswer().getId());
            questionAndAnswerResponse.setQuestion(quizItems.getQuestion());
            questionAndAnswerResponse.setUserAnswer(userAnswerResponse);
            List<Answers> allByQuizItems = answerRepository.getAllByQuizItems(quizItems);
            ArrayList<AnswerResponse> answerResponses = new ArrayList<>();
            for (Answers answers : allByQuizItems) {
                AnswerResponse answerResponse = new AnswerResponse();
                answerResponse.setAnswer(answers.getAnswer());
                answerResponse.setId(answers.getId());
                answerResponse.setIstrue(answers.isCorrect());
                answerResponses.add(answerResponse);

            }
            questionAndAnswerResponse.setAnswerResponses(answerResponses);
            questionAndAnswerResponses.add(questionAndAnswerResponse);
        }
        quizesInCurriculumItemResponse.setAnswerResponses(questionAndAnswerResponses);

        return quizesInCurriculumItemResponse;

    }


    public UserPerformeQuizeAndAnswersResponse getUserAnswersForQuizesByCurriculumItemCode(String curiyculumCode, String email) {

        authentication();
        GeneralUserProfile generalUserProfile = generalUserProfileRepository.getGeneralUserProfileByEmail(email);
        if (generalUserProfile == null)
            throw new ErrorException("Invalid User", VarList.RSP_NO_DATA_FOUND);
        SectionCurriculumItem sectionCurriculumItemByCode = sectionCurriculumItemRepository.getSectionCurriculumItemByCode(curiyculumCode);
        if (sectionCurriculumItemByCode == null)
            throw new ErrorException("Invalid curriculum Item Code", VarList.RSP_NO_DATA_FOUND);
        StudentHasCourse studentHasCourseByCourseAndGeneralUserProfile = studentHasCourseRepository.getStudentHasCourseByCourseAndGeneralUserProfile(sectionCurriculumItemByCode.getCourseSection().getCourse(), generalUserProfile);
        if (studentHasCourseByCourseAndGeneralUserProfile == null)
            throw new ErrorException("Course not Purchased", VarList.RSP_NO_DATA_FOUND);

        UserQuiz userQuizByQuizSectionCurriculumItem = userQuizeRepository.getUserQuizByQuizSectionCurriculumItem(sectionCurriculumItemByCode);
        if (userQuizByQuizSectionCurriculumItem == null)
            throw new ErrorException("Invalid Request", VarList.RSP_NO_DATA_FOUND);

        Quiz quizBySectionCurriculumItemId = quizeRepository.getQuizBySectionCurriculumItemId(sectionCurriculumItemByCode.getId());
        if (quizBySectionCurriculumItemId == null)
            throw new ErrorException("Invalid course code", VarList.RSP_NO_DATA_FOUND);


        List<QuizItems> allByQuizeId = quizeItemRepository.getAllByQuiz(userQuizByQuizSectionCurriculumItem.getQuiz());

        UserPerformeQuizeAndAnswersResponse quizesInCurriculumItemResponse = new UserPerformeQuizeAndAnswersResponse();
        ArrayList<UserPerformAnswerResponse> questionAndAnswerResponses = new ArrayList<>();
        for (QuizItems quizItems : allByQuizeId) {

            UserPerformAnswerResponse questionAndAnswerResponse = new UserPerformAnswerResponse();
            questionAndAnswerResponse.setQuestionItemCode(quizItems.getCode());
            UserAnswers userAnswersByQuizItem = userAnswerRepository.getUserAnswersByQuizItem(quizItems);
            AnswerResponse userAnswerResponse = new AnswerResponse();
            userAnswerResponse.setAnswer(userAnswersByQuizItem.getUserAnswer().getAnswer());
            userAnswerResponse.setIstrue(userAnswersByQuizItem.getUserAnswer().isCorrect());
            userAnswerResponse.setId(userAnswersByQuizItem.getUserAnswer().getId());
            questionAndAnswerResponse.setQuestion(quizItems.getQuestion());
            questionAndAnswerResponse.setUserAnswer(userAnswerResponse);
            List<Answers> allByQuizItems = answerRepository.getAllByQuizItems(quizItems);
            ArrayList<AnswerResponse> answerResponses = new ArrayList<>();
            for (Answers answers : allByQuizItems) {
                AnswerResponse answerResponse = new AnswerResponse();
                answerResponse.setAnswer(answers.getAnswer());
                answerResponse.setId(answers.getId());
                answerResponse.setIstrue(answers.isCorrect());
                answerResponses.add(answerResponse);

            }
            questionAndAnswerResponse.setAnswerResponses(answerResponses);
            questionAndAnswerResponses.add(questionAndAnswerResponse);
        }
        quizesInCurriculumItemResponse.setAnswerResponses(questionAndAnswerResponses);

        return quizesInCurriculumItemResponse;

    }

    @Override
    public SuccessResponse updateSectionTitleResponse(UpdateSectionTitleRequest updateSectionTitleRequest) {
        authentication();
        String sectionCode = updateSectionTitleRequest.getSectionCode();
        String title = updateSectionTitleRequest.getTitle();

        if (sectionCode == null || sectionCode.isEmpty() || title == null || title.isEmpty()) {
            throw new ErrorException("Invalid Request", VarList.RSP_NO_DATA_FOUND);
        }
        CourseSection courseSectionBySectionCode = courseSectionRepository.getCourseSectionBySectionCode(sectionCode);
        if (courseSectionBySectionCode == null) {
            throw new ErrorException("Invalid SectionCode", VarList.RSP_NO_DATA_FOUND);
        }
        if (courseSectionBySectionCode.getSectionName().equals(title)) {
            throw new ErrorException("Same Lesson Name", VarList.RSP_NO_DATA_FOUND);
        }
        CourseSection courseSectionBySectionName = courseSectionRepository.getCourseSectionBySectionName(title);

        if (courseSectionBySectionName != null) {
            if (courseSectionBySectionName.getSectionCode().equals(sectionCode)) {
                throw new ErrorException("Lesson Name Already Registred", VarList.RSP_NO_DATA_FOUND);
            }
        }

        courseSectionBySectionCode.setSectionName(title);

        courseSectionRepository.save(courseSectionBySectionCode);

        SuccessResponse successResponse = new SuccessResponse();
        successResponse.setVariable("200");
        successResponse.setMessage("Lesson Name Change Success");
        return successResponse;
    }

    @Override
    public SuccessResponse updateSectionCurriItem(UpdateSectionCurriItemRequest updateSectionCurriItemRequest) {

        authentication();
        String sectionCode = updateSectionCurriItemRequest.getSectionCode();
        String title = updateSectionCurriItemRequest.getTitle();
        String description = updateSectionCurriItemRequest.getDescription();
        String curriCode = updateSectionCurriItemRequest.getCurriCode();

        if (sectionCode == null || sectionCode.isEmpty() || title == null || title.isEmpty() || description == null || description.isEmpty() || curriCode == null || curriCode.isEmpty()) {
            throw new ErrorException("Invalid Request", VarList.RSP_NO_DATA_FOUND);
        }
        CourseSection courseSectionBySectionCode = courseSectionRepository.getCourseSectionBySectionCode(sectionCode);
        if (courseSectionBySectionCode == null) {
            throw new ErrorException("Invalid SectionCode", VarList.RSP_NO_DATA_FOUND);
        }
        SectionCurriculumItem sectionCurriculumItemByCode = sectionCurriculumItemRepository.getSectionCurriculumItemByCode(curriCode);
        if (sectionCurriculumItemByCode == null) {
            throw new ErrorException("Invalid Curriculum Item Code", VarList.RSP_NO_DATA_FOUND);
        }

        sectionCurriculumItemByCode.setTitle(title);
        sectionCurriculumItemByCode.setDescription(description);

        sectionCurriculumItemRepository.save(sectionCurriculumItemByCode);

        SuccessResponse successResponse = new SuccessResponse();
        successResponse.setVariable("200");
        successResponse.setMessage("Lesson Item Updated Success");
        return successResponse;
    }

    @Override
    public SuccessResponse updateVideo(UpdateVideoRequest updateVideoRequest) {

        authentication();
        final String courseCode = updateVideoRequest.getCourseCode();
        int id = updateVideoRequest.getId();
        final String curriculumItemCode = updateVideoRequest.getCurriculumItemCode();
        final String generatedVideoName = updateVideoRequest.getGeneratedVideoName();
        final Double videoLength = updateVideoRequest.getVideoLength();
        final String originalVideoName = updateVideoRequest.getOriginalVideoName();

        if (originalVideoName == null || originalVideoName.isEmpty() || id == 0) {
            throw new ErrorException("Invalid Request", VarList.RSP_NO_DATA_FOUND);
        }

        CurriculumItemFile referenceById = curriculumItemFileRepository.getReferenceById(id);

        boolean changStat = false;
        if (generatedVideoName != null) {
            referenceById.setUrl(Config.VIDEOS_UPLOAD_URL + generatedVideoName);
            referenceById.setVideoLength(videoLength);
            changStat = true;
        }

        if (!originalVideoName.equals(referenceById.getTitle())) {
            changStat = true;
            referenceById.setTitle(originalVideoName);
        }

        if (changStat) {
            SuccessResponse successResponse = new SuccessResponse();
            successResponse.setMessage("Lecture video Updated successfully");
            successResponse.setVariable(VarList.RSP_SUCCESS);
            curriculumItemFileRepository.save(referenceById);
            return successResponse;


        } else {
            throw new ErrorException("Change Properties to update the video", VarList.RSP_NO_DATA_FOUND);

        }


//
//        CurriculumItemFileType curriculumItemFileType = curriculumItemFileTypeRepository.getCurriculumItemFileTypeById(1);
//
//        if (curriculumItemFileType == null)
//            throw new ErrorException("Curriculum item file type not found", VarList.RSP_NO_DATA_FOUND);
//
//
//        if (courseCode == null || courseCode.isEmpty() || curriculumItemCode == null || curriculumItemCode.isEmpty() || generatedVideoName == null || generatedVideoName.isEmpty() || videoLength == null || videoLength.toString().isEmpty() || originalVideoName == null || originalVideoName.isEmpty())
//            throw new ErrorException("Invalid request", VarList.RSP_NO_DATA_FOUND);
//        Course course = courseRepository.getCourseByCode(courseCode);
//        if (course == null) throw new ErrorException("Invalid course code", VarList.RSP_NO_DATA_FOUND);
//        SectionCurriculumItem sectionCurriculumItem = sectionCurriculumItemRepository.getSectionCurriculumItemByCode(curriculumItemCode);
//        if (sectionCurriculumItem == null)
//            throw new ErrorException("Invalid curriculum item code", VarList.RSP_NO_DATA_FOUND);
//        if (!sectionCurriculumItem.getCourseSection().getCourse().equals(course))
//            throw new ErrorException("Section Curriculum Item Not applicable to course.", VarList.RSP_NO_DATA_FOUND);
//
//
//        CurriculumItemFile curriculumItemFile = new CurriculumItemFile();
//        curriculumItemFile.setTitle(originalVideoName);
//        curriculumItemFile.setUrl(Config.VIDEOS_UPLOAD_URL + generatedVideoName);
//        curriculumItemFile.setVideoLength(videoLength);
//        curriculumItemFile.setSectionCurriculumItem(sectionCurriculumItem);
//        curriculumItemFile.setCurriculumItemFileTypes(curriculumItemFileType);
//        curriculumItemFileRepository.save(curriculumItemFile);


    }

    @Override
    public SuccessResponse studentSubmitMcq(UserMcqRequest userMcqRequest) {
        GeneralUserProfile generalUserProfile = userAuthentication();
        List<UserSubmitMcqItemRequest> userSubmitMcqItemRequest = userMcqRequest.getUserSubmitMcqItemRequest();
        if (userSubmitMcqItemRequest == null)
            throw new ErrorException("Invalid Request", VarList.RSP_NO_DATA_FOUND);
        SectionCurriculumItem sectionCurriculumItemByCode = sectionCurriculumItemRepository.getSectionCurriculumItemByCode(userMcqRequest.getCurriculumItemCode());
        if (sectionCurriculumItemByCode == null)
            throw new ErrorException("Invalid Course Request", VarList.RSP_NO_DATA_FOUND);
        Quiz quizBySectionCurriculum = quizeRepository.getQuizBySectionCurriculumItem(sectionCurriculumItemByCode);
        if (quizBySectionCurriculum == null)
            throw new ErrorException("No quiz available for course", VarList.RSP_NO_DATA_FOUND);

        UserQuiz userQuizByQuizSectionCurriculumItem = userQuizeRepository.getUserQuizByQuizSectionCurriculumItem(sectionCurriculumItemByCode);
        if (userQuizByQuizSectionCurriculumItem != null)
            throw new ErrorException("Already Submit The Quiz", VarList.RSP_NO_DATA_FOUND);
        UserQuiz userQuiz = new UserQuiz();
        userQuiz.setUser(generalUserProfile);
        userQuiz.setQuiz(quizBySectionCurriculum);
        userQuizeRepository.save(userQuiz);
        for (UserSubmitMcqItemRequest mcq : userSubmitMcqItemRequest) {
            UserAnswers userAnswers = new UserAnswers();
            QuizItems quizItemsByCode = quizeItemRepository.getQuizItemsByCode(mcq.getMcqItemCode());
            Answers referenceById = answerRepository.getReferenceById(mcq.getUserAnswer());
            userAnswers.setUserAnswer(referenceById);
            userAnswers.setQuizItem(quizItemsByCode);
            userAnswers.setUserQuiz(userQuiz);
            userAnswerRepository.save(userAnswers);
        }

//        check the lesson is complete

        Course course = quizBySectionCurriculum.getSectionCurriculumItem().getCourseSection().getCourse();

        List<CourseSection> courseSectionByCourse = courseSectionRepository.getCourseSectionByCourse(course);
        if (courseSectionByCourse != null) {
            CourseSection courseSection = courseSectionByCourse.get(courseSectionByCourse.size() - 1);
            if (quizBySectionCurriculum.getSectionCurriculumItem().getCourseSection() == courseSection) {

                List<SectionCurriculumItem> sectionCurriculumItemByCourseSection = sectionCurriculumItemRepository.getSectionCurriculumItemByCourseSection(courseSection);
                if (sectionCurriculumItemByCourseSection != null) {
                    SectionCurriculumItem sectionCurriculumItem = sectionCurriculumItemByCourseSection.get(sectionCurriculumItemByCourseSection.size() - 1);
                    if (quizBySectionCurriculum.getSectionCurriculumItem() == sectionCurriculumItem) {

                        boolean lessonComplete = false;

                        Quiz quizBySectionCurriculumItem = quizeRepository.getQuizBySectionCurriculumItem(sectionCurriculumItem);
                        if (quizBySectionCurriculumItem != null) {
                            UserQuiz userQuizByQuizSectionCurriculum = userQuizeRepository.getUserQuizByQuizSectionCurriculumItem(sectionCurriculumItem);
                            if (userQuizByQuizSectionCurriculum != null) {
                                lessonComplete = true;
                            } else {
                                lessonComplete = false;
                            }
                        }
                        List<CurriculumItemZipFile> curriculumItemFileBySectionCurriculumItem = curriculumItemZipFileRepository.getCurriculumItemFileBySectionCurriculumItem(sectionCurriculumItem);
                        if (curriculumItemFileBySectionCurriculumItem != null && !curriculumItemFileBySectionCurriculumItem.isEmpty()) {
                            CurriculumItemZipFile curriculumItemZipFile = curriculumItemFileBySectionCurriculumItem.get(curriculumItemFileBySectionCurriculumItem.size() - 1);
                            if (curriculumItemZipFile != null) {
                                lessonComplete = true;
                            } else {
                                lessonComplete = false;
                            }
                        }

                        if (lessonComplete) {
                            StudentHasCourse studentHasCourseByCourseAndGeneralUserProfile = studentHasCourseRepository.getStudentHasCourseByCourseAndGeneralUserProfile(course, generalUserProfile);
                            studentHasCourseByCourseAndGeneralUserProfile.setIsComplete((byte) 1);
                            studentHasCourseRepository.save(studentHasCourseByCourseAndGeneralUserProfile);
                        }


                    }
                }


            }
        }
//        check the lesson is complete

        SuccessResponse successResponse = new SuccessResponse();
        successResponse.setMessage("Mcq Question Successfully Completed");
        successResponse.setVariable("200");
        return successResponse;
    }


}
