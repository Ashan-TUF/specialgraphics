package uk.specialgraphics.api.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import uk.specialgraphics.api.entity.*;
import uk.specialgraphics.api.exception.ErrorException;
import uk.specialgraphics.api.payload.request.CourseRequest;
import uk.specialgraphics.api.payload.response.FileUploadResponse;
import uk.specialgraphics.api.payload.response.SuccessResponse;
import uk.specialgraphics.api.repository.*;
import uk.specialgraphics.api.service.CourseService;
import uk.specialgraphics.api.service.UserProfileService;
import uk.specialgraphics.api.utils.FileUploadUtil;
import uk.specialgraphics.api.utils.VarList;

import java.io.IOException;
import java.util.Date;
import java.util.UUID;

@Service
public class CourseServiceImpl implements CourseService {
    @Autowired
    private UserProfileService userProfileService;
    @Autowired
    private CourseCategoryRepository courseCategoryRepository;
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private ApprovalTypeRepository approvalTypeRepository;
    @Autowired
    private CourseKeywordRepository courseKeywordRepository;

    private SuccessResponse successResponse = new SuccessResponse();
    @Override
    public SuccessResponse addCourse(CourseRequest courseRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        GeneralUserProfile profile = userProfileService.getProfile(username);
        if (profile != null) {
            if (profile.getIsActive() == 1) {
                if (profile.getGupType().getId() == 1) {
                    final String courseTitle = courseRequest.getCourse_title();
                    final MultipartFile image = courseRequest.getImg();
                    final MultipartFile video = courseRequest.getTest_video();
                                        final int categoryId = courseRequest.getCourse_category_id();
                    final String[] keyword = courseRequest.getKeywords();
                    if (courseTitle.isEmpty() || courseTitle == null) {
                        throw new ErrorException("Please add a course title", VarList.RSP_NO_DATA_FOUND);
                    } else if (image == null || image.isEmpty()) {
                        throw new ErrorException("Please add a course's image", VarList.RSP_NO_DATA_FOUND);
                    } else if (video == null || video.isEmpty()) {
                        throw new ErrorException("Please add a course's test video", VarList.RSP_NO_DATA_FOUND);
                    } else if (!image.getContentType().startsWith("image/") || !image.getOriginalFilename().matches(".*\\.(jpg|jpeg|png|gif|bmp|webp)$")) {
                        throw new ErrorException("Invalid image file type or extension. Only image files are allowed.", VarList.RSP_NO_DATA_FOUND);
                    } else if (!video.getContentType().startsWith("video/") || !video.getOriginalFilename().matches(".*\\.(mp4|avi|mov|mkv|webm|wmv)$")) {
                        throw new ErrorException("Invalid video file type. Only video files are allowed.", VarList.RSP_NO_DATA_FOUND);
                    } else if (categoryId == 0) {
                        throw new ErrorException("Please add a category", VarList.RSP_NO_DATA_FOUND);
                    } else if (keyword.length != 5) {
                        throw new ErrorException("Please add a keyword", VarList.RSP_NO_DATA_FOUND);
                    } else {
                        CourseCategory courseCategory = courseCategoryRepository.getCourseCategoryById(categoryId);
                        Course getCourse = courseRepository.getCourseByCourseTitle(courseRequest.getCourse_title());
                        if (getCourse == null) {
                            Course course = new Course();
                            course.setCode(UUID.randomUUID().toString());
                            course.setCourseTitle(courseRequest.getCourse_title());

                            try {
                                FileUploadResponse imageUploadResponse = FileUploadUtil.saveFile(courseRequest.getImg());
                                FileUploadResponse videoUploadResponse = FileUploadUtil.saveFile(courseRequest.getTest_video());
                                course.setImg(imageUploadResponse.getFilename());
                                course.setTest_video(videoUploadResponse.getFilename());
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                            course.setCreatedDate(new Date());
                            try {
                                FileUploadResponse imageUploadResponse = FileUploadUtil.saveFile(courseRequest.getImg());
                                FileUploadResponse videoUploadResponse = FileUploadUtil.saveFile(courseRequest.getTest_video());
                                course.setImg(imageUploadResponse.getFilename());
                                course.setTest_video(videoUploadResponse.getFilename());
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                            course.setCreatedDate(new Date());
                            String customId = generateCustomUUID();
                            course.setReferralCode(customId);
                            if (courseCategory != null) {
                                course.setCourseCategory(courseCategory);
                            } else {
                                throw new ErrorException("Course category not available", VarList.RSP_NO_DATA_FOUND);
                            }
                            ApprovalType Courseapproval = approvalTypeRepository.getApprovalTypeById(1);
                            if (Courseapproval != null) {
                                course.setApprovalType(Courseapproval);
                            } else {
                                throw new ErrorException("Approval type not available", VarList.RSP_NO_DATA_FOUND);
                            }

                            course.setIsPaid(1);
                            course.setIsOwned((byte) 0);
                            courseRepository.save(course);
                            for (int i = 0; i < keyword.length; i++) {
                                CourseKeyword courseKeyword = new CourseKeyword();
                                courseKeyword.setName(keyword[i]);
                                courseKeyword.setCourse(course);
                                courseKeywordRepository.save(courseKeyword);
                            }

                            SuccessResponse successResponse = new SuccessResponse();


                            successResponse.setMessage("Course added successfully");
                            successResponse.setVariable(VarList.RSP_SUCCESS);
                            return successResponse;
                        } else {
                            throw new ErrorException("The course has already been added", VarList.RSP_NO_DATA_FOUND);
                        }
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
    public static String generateCustomUUID() {
        UUID uuid = UUID.randomUUID();
        String customUuid = uuid.toString().replace("-", "").toUpperCase();
        customUuid = customUuid.replaceAll("[^A-Z0-9]", "");
        return customUuid.substring(0, 15);
    }
}
