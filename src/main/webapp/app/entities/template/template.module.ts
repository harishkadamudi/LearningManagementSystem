import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';

import { AmoghServerSharedModule } from '../../shared';
import {
    TemplateService,
    TemplatePopupService,
    TemplateComponent,
    TemplateDetailComponent,
    TemplateDialogComponent,
    TemplatePopupComponent,
    TemplateDeletePopupComponent,
    TemplateDeleteDialogComponent,
    templateRoute,
    templatePopupRoute,
    TemplateResolvePagingParams,
} from './';

const ENTITY_STATES = [
    ...templateRoute,
    ...templatePopupRoute,
];

@NgModule({
    imports: [
        AmoghServerSharedModule,
        RouterModule.forChild(ENTITY_STATES)
    ],
    declarations: [
        TemplateComponent,
        TemplateDetailComponent,
        TemplateDialogComponent,
        TemplateDeleteDialogComponent,
        TemplatePopupComponent,
        TemplateDeletePopupComponent,
    ],
    entryComponents: [
        TemplateComponent,
        TemplateDialogComponent,
        TemplatePopupComponent,
        TemplateDeleteDialogComponent,
        TemplateDeletePopupComponent,
    ],
    providers: [
        TemplateService,
        TemplatePopupService,
        TemplateResolvePagingParams,
    ],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class AmoghServerTemplateModule {}
