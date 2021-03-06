package com.amogh.lms.service;

import com.amogh.lms.service.dto.AssessmentDTO;
import com.amogh.lms.service.dto.AssessmentExerciseDTO;

import java.util.List;
import java.util.Map;

/**
 * Service Interface for managing Assessment.
 */
public interface AssessmentService {

    /**
     * Save a assessment.
     *
     * @param assessmentDTO the entity to save
     * @return the persisted entity
     */
    AssessmentDTO save(AssessmentDTO assessmentDTO);

    /**
     * Get all the assessments.
     *
     * @return the list of entities
     */
    List<AssessmentDTO> findAll();

    /**
     * Get the "id" assessment.
     *
     * @param id the id of the entity
     * @return the entity
     */
    AssessmentDTO findOne(Long id);

    /**
     * Delete the "id" assessment.
     *
     * @param id the id of the entity
     */
    void delete(Long id);

    /**
     * The assessment DTO for the course
     * @param courseId the course id
     * @return the assessment DTO
     */
    AssessmentDTO findByCourseId(Long courseId);

    /**
     * Gets the exercises for the given coures id
     * @param courseId the course id
     * @return list of assessment exercise DTOs
     */
    List<AssessmentExerciseDTO> getExercisesForCourseId(Long courseId);

    /**
     * Updates the assessment stats
     * @param assessmentExerciseDTOS list of assessment exercise
     * @return stats on how many answers the user got right
     */
    Map<String, Number> updateAssessmentStats(List<AssessmentExerciseDTO> assessmentExerciseDTOS);

    /**
     * Returns true or false based on assessment's completeness
     * @return true if complete, false otherwise
     */
    Boolean getAssessmentCompleteness(AssessmentDTO assessmentDTO);
}
