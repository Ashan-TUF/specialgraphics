package uk.specialgraphics.api.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import uk.specialgraphics.api.entity.*;
import uk.specialgraphics.api.exception.ErrorException;
import uk.specialgraphics.api.payload.request.UploadZipRequest;
import uk.specialgraphics.api.payload.response.AllCountryResponse;
import uk.specialgraphics.api.payload.response.FileUploadResponse;
import uk.specialgraphics.api.payload.response.SuccessResponse;
import uk.specialgraphics.api.payload.response.UserAssignmentZipFileResponseAdmin;
import uk.specialgraphics.api.repository.*;
import uk.specialgraphics.api.service.AssignmentService;
import uk.specialgraphics.api.service.CountryService;
import uk.specialgraphics.api.service.UserProfileService;
import uk.specialgraphics.api.utils.FileUploadUtil;
import uk.specialgraphics.api.utils.VarList;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static uk.specialgraphics.api.config.Config.*;

@Service
@Slf4j
public class AssignmentServiceImpl implements AssignmentService {

    private final UserProfileService userProfileService;

    private final StudentHasCourseRepository studentHasCourseRepository;
    private final CourseRepository courseRepository;
    private final SectionCurriculumItemRepository sectionCurriculumItemRepository;
    private final UserFileRepository userFileRepository;
    private final CurriculumItemZipFileRepository curriculumItemZipFileRepository;
    private final CourseSectionRepository courseSectionRepository;
    private final CurriculumItemFileRepository curriculumItemFileRepository;
    private final QuizeRepository quizeRepository;
    private final UserQuizeRepository userQuizeRepository;

    @Autowired
    public AssignmentServiceImpl(UserProfileService userProfileService, StudentHasCourseRepository studentHasCourseRepository
            , CourseRepository courseRepository, SectionCurriculumItemRepository sectionCurriculumItemRepository, UserFileRepository userFileRepository
            , CurriculumItemZipFileRepository curriculumItemZipFileRepository, CourseSectionRepository courseSectionRepository, CurriculumItemFileRepository curriculumItemFileRepository, QuizeRepository quizeRepository, UserQuizeRepository userQuizeRepository) {
        this.userProfileService = userProfileService;
        this.studentHasCourseRepository = studentHasCourseRepository;
        this.courseRepository = courseRepository;
        this.sectionCurriculumItemRepository = sectionCurriculumItemRepository;
        this.userFileRepository = userFileRepository;
        this.curriculumItemZipFileRepository = curriculumItemZipFileRepository;
        this.courseSectionRepository = courseSectionRepository;
        this.curriculumItemFileRepository = curriculumItemFileRepository;
        this.quizeRepository = quizeRepository;
        this.userQuizeRepository = userQuizeRepository;
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
    public ResponseEntity<Resource> downloadAssignment(String name) {
        userAuthentication();
        try {
            // Path to the ZIP file (replace this with the actual file path)
            String url = UPLOAD_URL + ZIP_UPLOAD_URL + name;
            Path path = Paths.get(url);
            Resource resource = new UrlResource(path.toUri());

            if (resource.exists() || resource.isReadable()) {
                // Return the ZIP file with the correct headers
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                        .body(resource);
            } else {
                return ResponseEntity.status(404).build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    @Override
    public ResponseEntity<Resource> admindownloadAssignment(String name) {
        authentication();
        try {
            // Path to the ZIP file (replace this with the actual file path)
            String url = UPLOAD_URL + ZIP_UPLOAD_URL + name;
            Path path = Paths.get(url);
            Resource resource = new UrlResource(path.toUri());

            if (resource.exists() || resource.isReadable()) {
                // Return the ZIP file with the correct headers
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                        .body(resource);
            } else {
                return ResponseEntity.status(404).build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    @Override
    public ResponseEntity<Resource> downloadUserAssignment(String name) {
        authentication();
        try {
            // Path to the ZIP file (replace this with the actual file path)
            String url = UPLOAD_URL + USER_ZIP_UPLOAD_URL + name;
            Path path = Paths.get(url);
            Resource resource = new UrlResource(path.toUri());

            if (resource.exists() || resource.isReadable()) {
                // Return the ZIP file with the correct headers
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                        .body(resource);
            } else {
                return ResponseEntity.status(404).build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    @Override
    public SuccessResponse uploadAssignment(UploadZipRequest uploadZipRequest) {

        GeneralUserProfile generalUserProfile = userAuthentication();
        final MultipartFile zip = uploadZipRequest.getZip();

        if (zip == null) {
            throw new ErrorException("The course has already been added", VarList.RSP_NO_DATA_FOUND);
        }
        Course courseByCode = courseRepository.getCourseByCode(uploadZipRequest.getCourseCode());
        if (courseByCode == null) {
            throw new ErrorException("Invalid Course", VarList.RSP_NO_DATA_FOUND);
        }
        SectionCurriculumItem sectionCurriculumItemByCode = sectionCurriculumItemRepository.getSectionCurriculumItemByCode(uploadZipRequest.getCurriculumCode());
        if (sectionCurriculumItemByCode == null) {
            throw new ErrorException("Invalid Item", VarList.RSP_NO_DATA_FOUND);
        }
        CurriculumItemZipFile curriculumItemZipFileById = curriculumItemZipFileRepository.getCurriculumItemZipFileById(uploadZipRequest.getZipId());
        if (curriculumItemZipFileById == null) {
            throw new ErrorException("Invalid Zip Upload", VarList.RSP_NO_DATA_FOUND);
        }
        StudentHasCourse studentHasCourseByCourseAndGeneralUserProfile = studentHasCourseRepository.getStudentHasCourseByCourseAndGeneralUserProfile(courseByCode, generalUserProfile);
        if (studentHasCourseByCourseAndGeneralUserProfile == null) {
            throw new ErrorException("Course Not Purchased", VarList.RSP_NO_DATA_FOUND);
        }
        UserZipFile userZipFileByCurriculumItemZipFile = userFileRepository.getUserZipFileByCurriculumItemZipFileAndStudentHasCourse(curriculumItemZipFileById, studentHasCourseByCourseAndGeneralUserProfile);
        if (userZipFileByCurriculumItemZipFile != null) {
            throw new ErrorException("Assignment Already Submitted", VarList.RSP_NO_DATA_FOUND);
        }

        UserZipFile userZipFile = new UserZipFile();
        userZipFile.setStudentHasCourse(studentHasCourseByCourseAndGeneralUserProfile);
        userZipFile.setCurriculumItemZipFile(curriculumItemZipFileById);
        userZipFile.setUploadDate(new Date());
        try {
            FileUploadResponse imageUploadResponse = FileUploadUtil.saveFile(zip, "userZip");
            userZipFile.setUrl(imageUploadResponse.getUrl());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        userFileRepository.save(userZipFile);
        SuccessResponse successResponse = new SuccessResponse();
        successResponse.setMessage("Assignment Submit success");
        successResponse.setVariable(VarList.RSP_SUCCESS);
        return successResponse;
    }

    @Override
    public SuccessResponse markStudentAssignmentMarks(int id, double marks) {
        authentication();
        UserZipFile userZipFileById = userFileRepository.getUserZipFileById(id);


        if (userZipFileById == null) {
            throw new ErrorException("Invalid Assignment Id", VarList.RSP_NO_DATA_FOUND);
        }
        if (marks <= 0 || 10 < marks) {
            throw new ErrorException("Invalid Mark", VarList.RSP_NO_DATA_FOUND);
        } else {
            userZipFileById.setMarks(marks);
            userZipFileById.setStatus(true);
            userFileRepository.save(userZipFileById);
            try {

            FileUploadResponse imageUploadResponse = FileUploadUtil.deleteFile(userZipFileById.getUrl());
            }catch (Exception exception){

            }


            Course course = userZipFileById.getCurriculumItemZipFile().getSectionCurriculumItem().getCourseSection().getCourse();

            List<CourseSection> courseSectionByCourse = courseSectionRepository.getCourseSectionByCourse(course);
            if (courseSectionByCourse != null) {
                CourseSection courseSection = courseSectionByCourse.get(courseSectionByCourse.size() - 1);
                if (userZipFileById.getCurriculumItemZipFile().getSectionCurriculumItem().getCourseSection() == courseSection) {

                    List<SectionCurriculumItem> sectionCurriculumItemByCourseSection = sectionCurriculumItemRepository.getSectionCurriculumItemByCourseSection(courseSection);
                    if (sectionCurriculumItemByCourseSection != null) {
                        SectionCurriculumItem sectionCurriculumItem = sectionCurriculumItemByCourseSection.get(sectionCurriculumItemByCourseSection.size() - 1);
                        if (userZipFileById.getCurriculumItemZipFile().getSectionCurriculumItem() == sectionCurriculumItem) {

                            boolean lessonComplete = false;

                            Quiz quizBySectionCurriculumItem = quizeRepository.getQuizBySectionCurriculumItem(sectionCurriculumItem);
                            if (quizBySectionCurriculumItem != null) {
                                UserQuiz userQuizByQuizSectionCurriculumItem = userQuizeRepository.getUserQuizByQuizSectionCurriculumItem(sectionCurriculumItem);
                                if (userQuizByQuizSectionCurriculumItem != null) {
                                    lessonComplete = true;
                                } else {
                                    lessonComplete = false;
                                }
                            }
                            List<CurriculumItemZipFile> curriculumItemFileBySectionCurriculumItem = curriculumItemZipFileRepository.getCurriculumItemFileBySectionCurriculumItem(sectionCurriculumItem);
                            if (curriculumItemFileBySectionCurriculumItem != null) {
                                CurriculumItemZipFile curriculumItemZipFile = curriculumItemFileBySectionCurriculumItem.get(curriculumItemFileBySectionCurriculumItem.size() - 1);
                                if (userZipFileById.getCurriculumItemZipFile() == curriculumItemZipFile) {
                                    lessonComplete = true;
                                } else {
                                    lessonComplete = false;
                                }
                            }

                            if (lessonComplete) {
                                StudentHasCourse studentHasCourseByCourseAndGeneralUserProfile = studentHasCourseRepository.getStudentHasCourseByCourseAndGeneralUserProfile(course, userZipFileById.getStudentHasCourse().getGeneralUserProfile());
                                studentHasCourseByCourseAndGeneralUserProfile.setIsComplete((byte) 1);
                                studentHasCourseRepository.save(studentHasCourseByCourseAndGeneralUserProfile);
                            }


                        }
                    }


                }
            }
        }

//        check the lesson is complete


        SuccessResponse successResponse = new SuccessResponse();
        successResponse.setVariable("200");
        successResponse.setMessage("Marks Update Success");
        return successResponse;
    }

    @Override
    public Page<UserZipFile> getAllUserAssignments(int page, int size) {
        authentication();
//        List<UserZipFile> allByStatus = userFileRepository.getDataToAdmin();
//
//        List<UserAssignmentZipFileResponseAdmin> userAssignmentZipFileResponseAdmins = new ArrayList<>();
//        for (UserZipFile userZipFile : allByStatus) {
//            UserAssignmentZipFileResponseAdmin userAssignmentZipFileResponseAdmin = new UserAssignmentZipFileResponseAdmin();
//            userAssignmentZipFileResponseAdmin.setMarks(userZipFile.getMarks());
//            userAssignmentZipFileResponseAdmin.setUrl(userZipFile.getUrl());
//            userAssignmentZipFileResponseAdmin.setTitle(userZipFile.getCurriculumItemZipFile().getTitle());
//            userAssignmentZipFileResponseAdmin.setId(userZipFile.getId());
//            userAssignmentZipFileResponseAdmin.setCourse(userZipFile.getStudentHasCourse().getCourse().getCourseTitle());
//            userAssignmentZipFileResponseAdmin.setDate(userZipFile.getUploadDate().toString());
//            userAssignmentZipFileResponseAdmin.setSection(userZipFile.getCurriculumItemZipFile().getSectionCurriculumItem().getCourseSection().getSectionName());
//            userAssignmentZipFileResponseAdmin.setComplete(userZipFile.isStatus());
//            userAssignmentZipFileResponseAdmin.setEmail(userZipFile.getStudentHasCourse().getGeneralUserProfile().getEmail());
//            userAssignmentZipFileResponseAdmin.setFname(userZipFile.getStudentHasCourse().getGeneralUserProfile().getFirstName());
//            userAssignmentZipFileResponseAdmin.setLname(userZipFile.getStudentHasCourse().getGeneralUserProfile().getLastName());
//            userAssignmentZipFileResponseAdmins.add(userAssignmentZipFileResponseAdmin);
//        }
//        return userAssignmentZipFileResponseAdmins;


        Pageable pageable = PageRequest.of(page, size); // Specify page number and size
        return userFileRepository.getDataToAdmin(pageable);
    }
}
