import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpResponse, HttpErrorResponse } from '@angular/common/http';

import { Observable } from 'rxjs/Observable';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager, JhiAlertService } from 'ng-jhipster';

import { Exercise } from './exercise.model';
import { ExercisePopupService } from './exercise-popup.service';
import { ExerciseService } from './exercise.service';
import { Topic, TopicService } from '../topic';
import { Template, TemplateService } from '../template';

@Component({
    selector: 'jhi-exercise-dialog',
    templateUrl: './exercise-dialog.component.html'
})
export class ExerciseDialogComponent implements OnInit {

    exercise: Exercise;
    isSaving: boolean;

    topics: Topic[];

    templates: Template[];

    constructor(
        public activeModal: NgbActiveModal,
        private jhiAlertService: JhiAlertService,
        private exerciseService: ExerciseService,
        private topicService: TopicService,
        private templateService: TemplateService,
        private eventManager: JhiEventManager
    ) {
    }

    ngOnInit() {
        this.isSaving = false;
        this.topicService.query()
            .subscribe((res: HttpResponse<Topic[]>) => { this.topics = res.body; }, (res: HttpErrorResponse) => this.onError(res.message));
        this.templateService.query()
            .subscribe((res: HttpResponse<Template[]>) => { this.templates = res.body; }, (res: HttpErrorResponse) => this.onError(res.message));
    }

    clear() {
        this.activeModal.dismiss('cancel');
    }

    save() {
        this.isSaving = true;
        if (this.exercise.id !== undefined) {
            this.subscribeToSaveResponse(
                this.exerciseService.update(this.exercise));
        } else {
            this.subscribeToSaveResponse(
                this.exerciseService.create(this.exercise));
        }
    }

    private subscribeToSaveResponse(result: Observable<HttpResponse<Exercise>>) {
        result.subscribe((res: HttpResponse<Exercise>) =>
            this.onSaveSuccess(res.body), (res: HttpErrorResponse) => this.onSaveError());
    }

    private onSaveSuccess(result: Exercise) {
        this.eventManager.broadcast({ name: 'exerciseListModification', content: 'OK'});
        this.isSaving = false;
        this.activeModal.dismiss(result);
    }

    private onSaveError() {
        this.isSaving = false;
    }

    private onError(error: any) {
        this.jhiAlertService.error(error.message, null, null);
    }

    trackTopicById(index: number, item: Topic) {
        return item.id;
    }

    trackTemplateById(index: number, item: Template) {
        return item.id;
    }
}

@Component({
    selector: 'jhi-exercise-popup',
    template: ''
})
export class ExercisePopupComponent implements OnInit, OnDestroy {

    routeSub: any;

    constructor(
        private route: ActivatedRoute,
        private exercisePopupService: ExercisePopupService
    ) {}

    ngOnInit() {
        this.routeSub = this.route.params.subscribe((params) => {
            if ( params['id'] ) {
                this.exercisePopupService
                    .open(ExerciseDialogComponent as Component, params['id']);
            } else {
                this.exercisePopupService
                    .open(ExerciseDialogComponent as Component);
            }
        });
    }

    ngOnDestroy() {
        this.routeSub.unsubscribe();
    }
}
