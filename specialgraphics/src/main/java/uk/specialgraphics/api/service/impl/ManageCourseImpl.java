package uk.specialgraphics.api.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import uk.specialgraphics.api.entity.*;
import uk.specialgraphics.api.exception.ErrorException;
import uk.specialgraphics.api.payload.request.IntendedLearnersRequest;
import uk.specialgraphics.api.payload.response.SuccessResponse;
import uk.specialgraphics.api.repository.*;
import uk.specialgraphics.api.service.ManageCourseService;
import uk.specialgraphics.api.service.UserProfileService;
import uk.specialgraphics.api.utils.VarList;

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
    private CourseIntentedLearnerRepository courseIntentedLearnerRepository;
    @Autowired
    private IntendedLearnerTypeRepository intendedLearnerTypeRepository;

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
