package uk.specialgraphics.api.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import uk.specialgraphics.api.config.Config;
import uk.specialgraphics.api.entity.*;
import uk.specialgraphics.api.exception.ErrorException;
import uk.specialgraphics.api.payload.request.CourseLandingPageRequest;
import uk.specialgraphics.api.payload.request.IntendedLearnersRequest;
import uk.specialgraphics.api.payload.response.CourseLandingPageResponse;
import uk.specialgraphics.api.payload.response.FileUploadResponse;
import uk.specialgraphics.api.payload.response.SuccessResponse;
import uk.specialgraphics.api.repository.*;
import uk.specialgraphics.api.service.ManageCourseService;
import uk.specialgraphics.api.service.UserProfileService;
import uk.specialgraphics.api.utils.FileUploadUtil;
import uk.specialgraphics.api.utils.VarList;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

@Service
public class ManageCourseImpl implements ManageCourseService {
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private UserProfileService userProfileService;
    @Autowired
    private LanguageRepository languageRepository;
    @Autowired
    private CourseLevelRepository courseLevelRepository;
    @Autowired
    private CourseLandingPageRepository courseLandingPageRepository;
    @Autowired
    private CourseCompleteRepository courseCompleteRepository;
    @Autowired
    private CourseIntentedLearnerRepository courseIntentedLearnerRepository;
    @Autowired
    private IntendedLearnerTypeRepository intendedLearnerTypeRepository;

    @Override
    public SuccessResponse saveCourseLandingPage(CourseLandingPageRequest courseLandingPageRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        GeneralUserProfile profile = userProfileService.getProfile(username);
        if (profile != null) {
            if (profile.getIsActive() == 1) {
                if (profile.getGupType().getId() == 2) {
                    final String course_code = courseLandingPageRequest.getCourseCode();
                    final String course_title = courseLandingPageRequest.getCourse_tile();
                    final String course_subtitle = courseLandingPageRequest.getCourse_subtitle();
                    final String course_description = courseLandingPageRequest.getDescription();
                    final int language = courseLandingPageRequest.getLanguage();
                    final int level = courseLandingPageRequest.getLevel();
                    final MultipartFile image = courseLandingPageRequest.getCourse_image();
                    final MultipartFile video = courseLandingPageRequest.getPromotional_video();

                    if (course_code == null || course_code.isEmpty()) {
                        throw new ErrorException("Invalid request", VarList.RSP_NO_DATA_FOUND);
                    } else if (course_title.isEmpty()) {
                        throw new ErrorException("Please add course's title", VarList.RSP_NO_DATA_FOUND);
                    } else if (course_subtitle.isEmpty()) {
                        throw new ErrorException("Please add course's subtitle", VarList.RSP_NO_DATA_FOUND);
                    } else if (course_description.isEmpty()) {
                        throw new ErrorException("Please add course's description", VarList.RSP_NO_DATA_FOUND);
                    } else if (level == 0) {
                        throw new ErrorException("Please select level", VarList.RSP_NO_DATA_FOUND);
                    }

                    Course course = courseRepository.findByCode(course_code);
                    if (course == null) {
                        throw new ErrorException("Invalid course code", VarList.RSP_NO_DATA_FOUND);
                    }

                    Language languageObj = languageRepository.getLanguageById(language);
                    if (languageObj == null) {
                        throw new ErrorException("Invalid language id", VarList.RSP_NO_DATA_FOUND);
                    }

                    CourseLevel courseLevel = courseLevelRepository.getCourseLevelById(level);
                    if (courseLevel == null) {
                        throw new ErrorException("Invalid course level id", VarList.RSP_NO_DATA_FOUND);
                    }

                    CourseLandingPage courseLandingPage = courseLandingPageRepository.findByCourseId(course.getId());

                    SuccessResponse successResponse = new SuccessResponse();

                    if (courseLandingPage != null) {

                        if (image != null && !image.isEmpty()) {

                            if (!image.getContentType().startsWith("image/") || !image.getOriginalFilename().matches(".*\\.(jpg|jpeg|png|gif|bmp)$")) {
                                throw new ErrorException("Invalid image file type or extension. Only image files are allowed.", VarList.RSP_NO_DATA_FOUND);
                            }
                            try {
                                final String OldImg = course.getImg();
                                FileUploadResponse imageUploadResponse = FileUploadUtil.saveFile(image);
                                course.setImg(imageUploadResponse.getFilename());
                                Files.delete(Paths.get(Config.UPLOAD_URL, OldImg));

                            } catch (Exception exception) {
                                throw new ErrorException(exception.getMessage(), VarList.RSP_NO_DATA_FOUND);
                            }

                        }
                        if (video != null && !video.isEmpty()) {

                            if (!video.getContentType().startsWith("video/") || !video.getOriginalFilename().matches(".*\\.(mp4|avi|mov|mkv|webm)$")) {
                                throw new ErrorException("Invalid video file type. Only video files are allowed.", VarList.RSP_NO_DATA_FOUND);
                            }
                            try {
                                final String OldVideo = courseLandingPage.getPromotionalVideoUrl();
                                FileUploadResponse videoUploadResponse = FileUploadUtil.saveFile(video);
                                courseLandingPage.setPromotionalVideoUrl(videoUploadResponse.getFilename());
                                Files.delete(Paths.get(Config.UPLOAD_URL, OldVideo));
                            } catch (Exception e) {
                                throw new ErrorException("Error", e.getMessage());
                            }
                        }
                        course.setCourseTitle(course_title);

                        courseLandingPage.setSubTitle(course_subtitle);
                        courseLandingPage.setDescription(course_description);
                        courseLandingPage.setLanguage(languageObj);
                        courseLandingPage.setCourseLevel(courseLevel);


                        successResponse.setMessage("Course landing page update successfully");

                    } else {
                        if (video == null || video.isEmpty()) {
                            throw new ErrorException("Please add a video", VarList.RSP_NO_DATA_FOUND);
                        } else if (!video.getContentType().startsWith("video/") || !video.getOriginalFilename().matches(".*\\.(mp4|avi|mov|mkv|webm)$")) {
                            throw new ErrorException("Invalid video file type. Only video files are allowed.", VarList.RSP_NO_DATA_FOUND);
                        }
                        try {
                            if (image != null && !image.isEmpty()) {

                                if (!image.getContentType().startsWith("image/") || !image.getOriginalFilename().matches(".*\\.(jpg|jpeg|png|gif|bmp)$")) {
                                    throw new ErrorException("Invalid image file type or extension. Only image files are allowed.", VarList.RSP_NO_DATA_FOUND);
                                }
                                try {
                                    final String OldImg = course.getImg();
                                    FileUploadResponse imageUploadResponse = FileUploadUtil.saveFile(image);
                                    course.setImg(imageUploadResponse.getFilename());
                                    Files.delete(Paths.get(Config.UPLOAD_URL, OldImg));

                                } catch (Exception exception) {
                                    throw new ErrorException(exception.getMessage(), VarList.RSP_NO_DATA_FOUND);
                                }

                            }

                            FileUploadResponse videoUploadResponse = FileUploadUtil.saveFile(video);

                            course.setCourseTitle(course_title);
                            courseLandingPage = new CourseLandingPage();
                            courseLandingPage.setSubTitle(course_subtitle);
                            courseLandingPage.setDescription(course_description);
                            courseLandingPage.setLanguage(languageObj);
                            courseLandingPage.setCourseLevel(courseLevel);
                            courseLandingPage.setPromotionalVideoUrl(videoUploadResponse.getFilename());
                            courseLandingPage.setCourse(course);

                            successResponse.setMessage("Course landing page added successfully");

                        } catch (Exception e) {
                            throw new ErrorException(e.getMessage(), VarList.RSP_NO_DATA_FOUND);
                        }
                    }

                    courseRepository.save(course);
                    courseLandingPageRepository.save(courseLandingPage);

                    CourseComplete courseComplete = courseCompleteRepository.getCourseCompleteByCourse(course);

                    if (courseComplete == null) {
                        courseComplete = new CourseComplete();
                        courseComplete.setCourse(course);
                    }
                    courseComplete.setCourseLandingPage((byte) 1);

                    courseCompleteRepository.save(courseComplete);

                    successResponse.setVariable(VarList.RSP_SUCCESS);
                    return successResponse;

                } else {
                    throw new ErrorException("You are not a instructor to this operation", VarList.RSP_NO_DATA_FOUND);
                }
            } else {
                throw new ErrorException("User not active", VarList.RSP_NO_DATA_FOUND);
            }
        } else {
            throw new ErrorException("User not found", VarList.RSP_NO_DATA_FOUND);
        }
    }

    @Override
    public CourseLandingPageResponse getCourseLandingPage(String courseCode) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        GeneralUserProfile profile = userProfileService.getProfile(username);
        if (profile != null) {
            if (profile.getIsActive() == 1) {
                if (profile.getGupType().getId() == 2 || profile.getGupType().getId() == 3) {
                    if (courseCode == null || courseCode.isEmpty()) {
                        throw new ErrorException("Invalid request", VarList.RSP_NO_DATA_FOUND);
                    } else {
                        Course course = courseRepository.findByCode(courseCode);
                        if (course != null) {
                            CourseLandingPage courseLandingPage = courseLandingPageRepository.getCourseLandingPageByCourse(course);
                            CourseLandingPageResponse courseLandingPageResponse = new CourseLandingPageResponse();
                            courseLandingPageResponse.setCourseTitle(course.getCourseTitle());
                            courseLandingPageResponse.setCourseLength(course.getCourseLength() == 0 ? 0 : course.getCourseLength());
                            courseLandingPageResponse.setCourseImage(course.getImg());

                            if (courseLandingPage != null) {
                                courseLandingPageResponse.setCourseSubTitle(courseLandingPage.getSubTitle());
                                courseLandingPageResponse.setDescription(courseLandingPage.getDescription());
                                courseLandingPageResponse.setLanguageId(courseLandingPage.getLanguage().getId().toString());
                                courseLandingPageResponse.setLanguage(courseLandingPage.getLanguage().getName());
                                courseLandingPageResponse.setLevelId(courseLandingPage.getCourseLevel().getId().toString());
                                courseLandingPageResponse.setLevel(courseLandingPage.getCourseLevel().getName());
                                courseLandingPageResponse.setPromotionalVideo(courseLandingPage.getPromotionalVideoUrl());
                            } else {
                                courseLandingPageResponse.setCourseSubTitle("");
                                courseLandingPageResponse.setDescription("");
                                courseLandingPageResponse.setLanguageId("");
                                courseLandingPageResponse.setLanguage("");
                                courseLandingPageResponse.setLevelId("");
                                courseLandingPageResponse.setLevel("");
                                courseLandingPageResponse.setPromotionalVideo("");
                            }
                            return courseLandingPageResponse;
                        } else {
                            throw new ErrorException("Invalid course code", VarList.RSP_NO_DATA_FOUND);
                        }
                    }
                } else {
                    throw new ErrorException("You are not a instructor or admin to this operation", VarList.RSP_NO_DATA_FOUND);
                }
            } else {
                throw new ErrorException("User not active", VarList.RSP_NO_DATA_FOUND);
            }
        } else {
            throw new ErrorException("User not found", VarList.RSP_NO_DATA_FOUND);
        }
    }

    @Override
    public SuccessResponse saveIntendedLearners(IntendedLearnersRequest intendedLearnersRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        GeneralUserProfile profile = userProfileService.getProfile(username);
        if (profile != null) {
            if (profile.getIsActive() == 1) {
                if (profile.getGupType().getId() == 2) {
                    final String[] studentsLearn = intendedLearnersRequest.getStudentsLearn();
                    final String[] requirements = intendedLearnersRequest.getRequirements();
                    final String[] who = intendedLearnersRequest.getWho();
                    final String course_code = intendedLearnersRequest.getCourse_code();

                    if (course_code.isEmpty()) {
                        throw new ErrorException("Invalid request", VarList.RSP_NO_DATA_FOUND);
                    } else if (studentsLearn == null || studentsLearn.length < 3) {
                        throw new ErrorException("Please add what students are learning", VarList.RSP_NO_DATA_FOUND);
                    } else if (requirements == null || requirements.length < 3) {
                        throw new ErrorException("Please add what the requirements are", VarList.RSP_NO_DATA_FOUND);
                    } else if (who.length < 1) {
                        throw new ErrorException("Please add who this course is for", VarList.RSP_NO_DATA_FOUND);
                    } else {
                        Course course = courseRepository.findByCode(course_code);
                        if (course != null) {
                            List<CourseIntentedLearner> courseIntentedLearnerinfo = courseIntentedLearnerRepository.getCourseIntentedLearnerByCourseId(course.getId());
                            if (courseIntentedLearnerinfo.size() > 0) {
                                int i = 0;
                                for (CourseIntentedLearner obj : courseIntentedLearnerinfo) {
                                    obj.setName(courseIntentedLearnerinfo.get(i).getName());
                                    i = i + 1;
                                    courseIntentedLearnerRepository.delete(obj);
                                }
                            }
                            for (int i = 0; i < studentsLearn.length; i++) {
                                CourseIntentedLearner courseIntentedLearner = new CourseIntentedLearner();
                                courseIntentedLearner.setName(studentsLearn[i]);
                                IntendedLearnerType intendedLearnerType = intendedLearnerTypeRepository.getIntendedLearnerTypeById(1);
                                if (intendedLearnerType == null)
                                    throw new ErrorException("Intended learner type not found", VarList.RSP_NO_DATA_FOUND);
                                courseIntentedLearner.setIntendedLearnerType(intendedLearnerType);
                                courseIntentedLearner.setCourse(course);
                                courseIntentedLearnerRepository.save(courseIntentedLearner);
                            }
                            for (int i = 0; i < requirements.length; i++) {
                                CourseIntentedLearner courseIntentedLearner1 = new CourseIntentedLearner();
                                courseIntentedLearner1.setName(requirements[i]);
                                IntendedLearnerType intendedLearnerType = intendedLearnerTypeRepository.getIntendedLearnerTypeById(2);
                                if (intendedLearnerType == null)
                                    throw new ErrorException("Intended learner type not found", VarList.RSP_NO_DATA_FOUND);
                                courseIntentedLearner1.setIntendedLearnerType(intendedLearnerType);
                                courseIntentedLearner1.setCourse(course);
                                courseIntentedLearnerRepository.save(courseIntentedLearner1);
                            }
                            for (int i = 0; i < who.length; i++) {
                                CourseIntentedLearner courseIntentedLearner2 = new CourseIntentedLearner();
                                courseIntentedLearner2.setName(who[i]);
                                IntendedLearnerType intendedLearnerType = intendedLearnerTypeRepository.getIntendedLearnerTypeById(3);
                                if (intendedLearnerType == null)
                                    throw new ErrorException("Intended learner type not found", VarList.RSP_NO_DATA_FOUND);
                                courseIntentedLearner2.setIntendedLearnerType(intendedLearnerType);
                                courseIntentedLearner2.setCourse(course);
                                courseIntentedLearnerRepository.save(courseIntentedLearner2);
                            }
                        } else {
                            throw new ErrorException("Invalid course code", VarList.RSP_NO_DATA_FOUND);
                        }

                        CourseComplete courseComplete = courseCompleteRepository.getCourseCompleteByCourse(course);

                        if (courseComplete == null) {
                            courseComplete = new CourseComplete();
                            courseComplete.setCourse(course);
                        }
                        courseComplete.setIntendedLearners((byte) 1);

                        courseCompleteRepository.save(courseComplete);
                        SuccessResponse successResponse = new SuccessResponse();

                        successResponse.setMessage("Intended learners added successfully");
                        successResponse.setVariable(VarList.RSP_SUCCESS);
                        return successResponse;
                    }
                } else {
                    throw new ErrorException("You are not a instructor to this operation", VarList.RSP_NO_DATA_FOUND);
                }
            } else {
                throw new ErrorException("User not active", VarList.RSP_NO_DATA_FOUND);
            }
        } else {
            throw new ErrorException("User not found", VarList.RSP_NO_DATA_FOUND);
        }
    }
}
