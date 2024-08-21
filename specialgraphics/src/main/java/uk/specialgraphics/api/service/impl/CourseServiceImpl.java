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
    private CourseRepository courseRepository;

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
                    final Double defaultPrice = courseRequest.getDefault_price();
                    final MultipartFile image = courseRequest.getImg();
                    final String description = courseRequest.getDescription();
                    final String video = courseRequest.getTest_video();


                    if (courseTitle.isEmpty() || courseTitle == null) {
                        throw new ErrorException("Please add a course title", VarList.RSP_NO_DATA_FOUND);
                    } else if (image == null || image.isEmpty()) {
                        throw new ErrorException("Please add a course's image", VarList.RSP_NO_DATA_FOUND);
                    } else if (video == null || video.isEmpty()) {
                        throw new ErrorException("Please add a course's test video", VarList.RSP_NO_DATA_FOUND);
                    } else if (!image.getContentType().startsWith("image/") || !image.getOriginalFilename().matches(".*\\.(jpg|jpeg|png|gif|bmp|webp)$")) {
                        throw new ErrorException("Invalid image file type or extension. Only image files are allowed.", VarList.RSP_NO_DATA_FOUND);
                    } else if (defaultPrice == null || defaultPrice.toString().isEmpty()) {
                        throw new ErrorException("Please add a default price", VarList.RSP_NO_DATA_FOUND);
                    } else if (description == null || description.isEmpty()) {
                        throw new ErrorException("Please add a default price", VarList.RSP_NO_DATA_FOUND);
                    } else {

                        Course getCourse = courseRepository.getCourseByCourseTitle(courseRequest.getCourse_title());
                        if (getCourse == null) {
                            Course course = new Course();
                            course.setCode(UUID.randomUUID().toString());
                            course.setCourseTitle(courseRequest.getCourse_title());
                            course.setPromotionalVideo(video);
                            try {
                                FileUploadResponse imageUploadResponse = FileUploadUtil.saveFile(courseRequest.getImg());
                                course.setImg(imageUploadResponse.getFilename());
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                            course.setCreatedDate(new Date());
                            course.setPrice(defaultPrice);
                            course.setIsActive((byte) 1);
                            courseRepository.save(course);

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
}
