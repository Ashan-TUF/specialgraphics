package uk.specialgraphics.api.service;

import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import uk.specialgraphics.api.entity.UserZipFile;
import uk.specialgraphics.api.payload.request.UploadZipRequest;
import uk.specialgraphics.api.payload.response.AllCountryResponse;
import uk.specialgraphics.api.payload.response.SuccessResponse;
import uk.specialgraphics.api.payload.response.UserAssignmentZipFileResponseAdmin;

import java.util.List;

public interface AssignmentService {

    ResponseEntity<Resource> downloadAssignment(String name);
    ResponseEntity<Resource> admindownloadAssignment(String name);
    ResponseEntity<Resource> downloadUserAssignment(String name);
    SuccessResponse uploadAssignment(UploadZipRequest uploadZipRequest);
    SuccessResponse markStudentAssignmentMarks(int id,double marks);

    Page<UserZipFile> getAllUserAssignments(int page, int size);
}
