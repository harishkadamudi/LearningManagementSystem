package com.amogh.lms.web.rest;

import com.amogh.lms.service.AssessmentService;
import com.amogh.lms.service.dto.AssessmentDTO;
import com.amogh.lms.service.dto.AssessmentDetailsDTO;
import com.amogh.lms.service.dto.AssessmentExerciseDTO;
import com.amogh.lms.service.dto.QuestionConfigDTO;
import com.amogh.lms.web.rest.errors.BadRequestAlertException;
import com.amogh.lms.web.rest.util.HeaderUtil;
import com.amogh.lms.web.rest.util.PaginationUtil;
import com.codahale.metrics.annotation.Timed;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

/**
 * REST controller for managing Assessment.
 */
@RestController
@RequestMapping("/api")
public class AssessmentResource {

    private final Logger log = LoggerFactory.getLogger(AssessmentResource.class);

    private static final String ENTITY_NAME = "assessment";

    private final AssessmentService assessmentService;


    public AssessmentResource(AssessmentService assessmentService) {
        this.assessmentService = assessmentService;
    }

    /**
     * POST  /assessments : Create a new assessment.
     *
     * @param assessmentDTO the assessmentDTO to create
     * @return the ResponseEntity with status 201 (Created) and with body the new assessmentDTO, or with status 400 (Bad Request) if the assessment has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/assessments")
    @Timed
    public ResponseEntity<AssessmentDTO> createAssessment(@Valid @RequestBody AssessmentDTO assessmentDTO) throws URISyntaxException {
        log.debug("REST request to save Assessment : {}", assessmentDTO);
        if (assessmentDTO.getId() != null) {
            throw new BadRequestAlertException("A new assessment cannot already have an ID", ENTITY_NAME, "idexists");
        }
        AssessmentDTO result = assessmentService.save(assessmentDTO);
        return ResponseEntity.created(new URI("/api/assessments/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /assessments : Updates an existing assessment.
     *
     * @param assessmentDTO the assessmentDTO to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated assessmentDTO,
     * or with status 400 (Bad Request) if the assessmentDTO is not valid,
     * or with status 500 (Internal Server Error) if the assessmentDTO couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/assessments")
    @Timed
    public ResponseEntity<AssessmentDTO> updateAssessment(@Valid @RequestBody AssessmentDTO assessmentDTO) throws URISyntaxException {
        log.debug("REST request to update Assessment : {}", assessmentDTO);
        if (assessmentDTO.getId() == null) {
            return createAssessment(assessmentDTO);
        }
        AssessmentDTO result = assessmentService.save(assessmentDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, assessmentDTO.getId().toString()))
            .body(result);
    }

    /**
     * GET  /assessments : get all the assessments.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of assessments in body
     */
    @GetMapping("/assessments")
    @Timed
    public ResponseEntity<List<AssessmentDetailsDTO>> getAllAssessments() {
        log.debug("REST request to get a page of Assessments");
        List<AssessmentDetailsDTO> assessmentDetailsDTOS = new ArrayList<>();
        List<AssessmentDTO> assessmentDTOS = assessmentService.findAll();
        for(AssessmentDTO assessmentDTO: assessmentDTOS) {
            Boolean assessmentCompleteness = this.assessmentService.getAssessmentCompleteness(assessmentDTO);
            AssessmentDetailsDTO assessmentDetailsDTO = new AssessmentDetailsDTO();
            assessmentDetailsDTO.setupDTO(assessmentDTO);
            assessmentDetailsDTO.setComplete(assessmentCompleteness);
        }
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(assessmentDetailsDTOS));
    }

    /**
     * DELETE  /assessments/:id : delete the "id" assessment.
     *
     * @param id the id of the assessmentDTO to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/assessments/{id}")
    @Timed
    public ResponseEntity<Void> deleteAssessment(@PathVariable Long id) {
        log.debug("REST request to delete Assessment : {}", id);
        assessmentService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }


    /**
     * POST /assessments/course : get the "id" assessment.
     *
     * @param questionConfigDTO the question config DTO with details to fetch
     * @return the ResponseEntity with status 200 (OK) and with body tthe list of exercise dtos, or with status 404 (Not Found)
     */
    @PostMapping("/assessments/exercises")
    @Timed
    public ResponseEntity<List<AssessmentExerciseDTO>> getExercisesForAssessment(@Valid @RequestBody QuestionConfigDTO questionConfigDTO) {
        log.debug("REST request to get questions for assessment for course : {}", questionConfigDTO.getCourseId());
        List<AssessmentExerciseDTO> exercisesForCourseId = this.assessmentService.getExercisesForCourseId(questionConfigDTO.getCourseId());
        List<AssessmentExerciseDTO> resultExerciseDTOList = null;
        if (exercisesForCourseId.size() <= questionConfigDTO.getNumberOfQuestions()) {
            resultExerciseDTOList = exercisesForCourseId;
        } else {
            Collections.shuffle(exercisesForCourseId);
            resultExerciseDTOList = new ArrayList<>();
            for (int i = 0; i < questionConfigDTO.getNumberOfQuestions(); i++) {
                resultExerciseDTOList.add(exercisesForCourseId.get(i));
            }
        }
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(resultExerciseDTOList));
    }


    /**
     * POST /assessments/course : get the "id" assessment.
     *
     * @param assessmentExerciseDTOS the question config DTO with details to fetch
     * @return the ResponseEntity with status 200 (OK) and with body tthe list of exercise dtos, or with status 404 (Not Found)
     */
    @PostMapping("/assessments/submit")
    @Timed
    public ResponseEntity<Map<String, Number>> updateAssessmentStats(@Valid @RequestBody List<AssessmentExerciseDTO> assessmentExerciseDTOS) {
        log.debug("REST request to store stats for assessment");
        Map<String, Number> results = this.assessmentService.updateAssessmentStats(assessmentExerciseDTOS);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(results));
    }

}
