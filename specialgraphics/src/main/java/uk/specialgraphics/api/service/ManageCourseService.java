package uk.specialgraphics.api.service;

import uk.specialgraphics.api.payload.request.IntendedLearnersRequest;
import uk.specialgraphics.api.payload.response.SuccessResponse;

public interface ManageCourseService {
    SuccessResponse saveIntendedLearners(IntendedLearnersRequest intendedLearnersRequest);
}
