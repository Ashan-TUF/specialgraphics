package uk.specialgraphics.api.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import uk.specialgraphics.api.entity.*;
import uk.specialgraphics.api.exception.ErrorException;
import uk.specialgraphics.api.payload.request.CourseRequest;
import uk.specialgraphics.api.payload.request.SingleCourseRequest;
import uk.specialgraphics.api.payload.response.*;
import uk.specialgraphics.api.repository.*;
import uk.specialgraphics.api.service.CourseService;
import uk.specialgraphics.api.service.UserProfileService;
import uk.specialgraphics.api.utils.FileUploadUtil;
import uk.specialgraphics.api.utils.VarList;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
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
                if (profile.getGupType().getId() == 2) {
                    final String courseTitle = courseRequest.getCourse_title();
                    final Double defaultPrice = courseRequest.getDefault_price();
                    final MultipartFile image = courseRequest.getImg();
                    final String description = courseRequest.getDescription();
                    final String video = courseRequest.getPromotonalVideo();
                    final String points = courseRequest.getPoints();


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
                        throw new ErrorException("Please add a description", VarList.RSP_NO_DATA_FOUND);
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
                            course.setDescription(description);
                            course.setPoints(points);
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

    @Override
    public AllCourseResponse getAllCourses() {
        List<Course> courseList = courseRepository.getAllByIsActiveEquals((byte) 1);
        if (courseList.isEmpty()) {
            log.warn("password incorrect.");
            throw new ErrorException("Empty Courses", VarList.RSP_NO_DATA_FOUND);
        }
        AllCourseResponse allCourseResponse = new AllCourseResponse();
        allCourseResponse.setCourseList(courseList);
        return allCourseResponse;

    }

    @Override
    public SingleCourseResponse getCourseByCode(SingleCourseRequest request) {
        Course courseByCode = courseRepository.getCourseByCode(request.getCode());
        if (courseByCode == null) {
            log.warn("Invalid Course Code");
            throw new ErrorException("Invalid Course Code", VarList.RSP_NO_DATA_FOUND);
        }

        SingleCourseResponse singleCourseResponse = new SingleCourseResponse();
        singleCourseResponse.setCourse(courseByCode);
        return singleCourseResponse;
    }

    @Override
    public SuccessResponse updateCourseByCode(CourseRequest request) {
        Course courseByCode = courseRepository.getCourseByCode(request.getCode());


        if (courseByCode == null) {
            log.warn("Invalid Course Code");
            throw new ErrorException("Invalid Course Code", VarList.RSP_NO_DATA_FOUND);
        }
        boolean detailsChanged = false;

        if (!courseByCode.getPrice().equals(request.getDefault_price())) {
            courseByCode.setPrice(request.getDefault_price());
            detailsChanged = true;
        }
        if (!courseByCode.getCourseTitle().equals(request.getCourse_title())) {
            courseByCode.setCourseTitle(request.getCourse_title());
            detailsChanged = true;

        }
        if (!courseByCode.getDescription().equals(request.getDescription())) {
            courseByCode.setDescription(request.getDescription());
            detailsChanged = true;

        }
        if (!courseByCode.getPoints().equals(request.getPoints())) {
            courseByCode.setPoints(request.getPoints());
            detailsChanged = true;

        }
        if (!courseByCode.getPromotionalVideo().equals(request.getPromotonalVideo())) {
            courseByCode.setPromotionalVideo(request.getPromotonalVideo());
            detailsChanged = true;

        }

        if (request.getImg() != null) {
            try {
                FileUploadResponse imageUploadResponse = FileUploadUtil.saveFile(request.getImg());
                courseByCode.setImg(imageUploadResponse.getFilename());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            detailsChanged = true;
            System.out.println("file");
        }


        if (!detailsChanged) {
            log.warn("Same Course Details");
            throw new ErrorException("Same Course Details", VarList.RSP_NO_DATA_FOUND);
        }
        courseRepository.save(courseByCode);
        SuccessResponse successResponse = new SuccessResponse();
        successResponse.setMessage("Course Updated");
        successResponse.setVariable("200");
        return successResponse;



    }
}
