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
@Slf4j
public class CourseServiceImpl implements CourseService {
    private final UserProfileService userProfileService;
    private final CourseRepository courseRepository;

    @Autowired
    public CourseServiceImpl(UserProfileService userProfileService,
                             CourseRepository courseRepository) {
        this.userProfileService = userProfileService;
        this.courseRepository = courseRepository;
    }


    private void authentication() {
        Authentication authentication;
        String username;
        GeneralUserProfile profile;
        authentication = SecurityContextHolder.getContext().getAuthentication();
        username = authentication.getName();
        profile = userProfileService.getProfile(username);

        if (profile == null) {
            throw new ErrorException("User not found", VarList.RSP_NO_DATA_FOUND);
        }
        if (profile.getIsActive() != 1) {
            throw new ErrorException("User not active", VarList.RSP_NO_DATA_FOUND);
        }
        if (profile.getGupType().getId() != 1) {
            throw new ErrorException("You are not a instructor to this operation", VarList.RSP_NO_DATA_FOUND);
        }
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
        List<CourseResponse> courseRespons = new ArrayList<>();
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
                courseRespons.add(courseResponse);
            }
        }
        return courseRespons;

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
}
