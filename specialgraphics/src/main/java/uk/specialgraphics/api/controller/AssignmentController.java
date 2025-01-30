package uk.specialgraphics.api.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import uk.specialgraphics.api.entity.GeneralUserProfile;
import uk.specialgraphics.api.entity.UserZipFile;
import uk.specialgraphics.api.exception.ErrorException;
import uk.specialgraphics.api.payload.request.UploadZipRequest;
import uk.specialgraphics.api.payload.response.AllCountryResponse;
import uk.specialgraphics.api.payload.response.SuccessResponse;
import uk.specialgraphics.api.payload.response.UserAssignmentZipFileResponseAdmin;
import uk.specialgraphics.api.service.AssignmentService;
import uk.specialgraphics.api.service.CountryService;
import uk.specialgraphics.api.service.CourseService;
import uk.specialgraphics.api.service.UserProfileService;
import uk.specialgraphics.api.utils.VarList;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static uk.specialgraphics.api.config.Config.UPLOAD_URL;
import static uk.specialgraphics.api.config.Config.ZIP_UPLOAD_URL;

@RestController
@RequestMapping("/assignments")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Slf4j
public class AssignmentController {

    @Autowired
    AssignmentService assignmentService;

    @Autowired
    UserProfileService userProfileService;


    @GetMapping("/downloadZipFile/zip/{name}")
    public ResponseEntity<Resource> downloadZipFile(@PathVariable String name) {

        return assignmentService.downloadAssignment(name);
    }

    @GetMapping("/admindownloadZipFile/zip/{name}")
    public ResponseEntity<Resource> admindownloadZipFile(@PathVariable String name) {

        return assignmentService.admindownloadAssignment(name);
    }

    @PostMapping("/uploadZipFile")
    public SuccessResponse uploadZipFile(UploadZipRequest uploadZipRequest) {

        return assignmentService.uploadAssignment(uploadZipRequest);
    }

    @GetMapping("/downloadUserZipFile/userZip/{name}")
    public ResponseEntity<Resource> downloaduserZipFile(@PathVariable String name) {

        return assignmentService.downloadUserAssignment(name);
    }

    @PostMapping("/updateStudentMarks/{id}/{marks}")
    public SuccessResponse updateAssignmentMarks(@PathVariable int id, @PathVariable double marks) {

        return assignmentService.markStudentAssignmentMarks(id, marks);
    }

    @GetMapping("/getAllUserAssignments")
    public ResponseEntity<Page<UserZipFile>> getAdminData(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<UserZipFile> data = assignmentService.getAllUserAssignments(page, size);
        return new ResponseEntity<>(data, HttpStatus.OK);
    }


}
