package com.syncrotess.openfriday.core;

import com.syncrotess.openfriday.entities.Timeslot;
import com.syncrotess.openfriday.entities.Workshop;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.ConstraintFactory;
import org.optaplanner.core.api.score.stream.ConstraintProvider;
import org.optaplanner.core.api.score.stream.Joiners;

public class TimetableConstraintProvider implements ConstraintProvider {
    @Override
    public Constraint[] defineConstraints(ConstraintFactory constraintFactory) {
        return new Constraint[] {
            roomConflict(constraintFactory),
            workshopNotAvailableConflict(constraintFactory),
            workshopNotUsedPunishment(constraintFactory),
            onlyOneVariableNullConflict(constraintFactory),
            roomNotAvailableConflict(constraintFactory)
        };
    }

    private Constraint roomNotAvailableConflict(ConstraintFactory constraintFactory) {
        return constraintFactory
                .from(Workshop.class)
                .filter(workshop -> workshop.getRoom() != null
                                    && workshop.getTimeslot() != null
                                    && !workshop.getRoom().getTimeslots().contains(workshop.getTimeslot()))
                .penalize("Room not available conflict", HardSoftScore.ofHard(5));
    }

    private Constraint workshopNotUsedPunishment(ConstraintFactory constraintFactory) {
        return constraintFactory
                .from(Workshop.class)
                .filter(workshop -> workshop.getTimeslot() == null && workshop.getRoom() == null)
                .penalize("Workshop not used", HardSoftScore.ONE_HARD);
    }

    private Constraint onlyOneVariableNullConflict(ConstraintFactory constraintFactory) {
        return constraintFactory
                .from(Workshop.class)
                .filter(workshop -> workshop.getRoom() != null && workshop.getTimeslot() == null || workshop.getRoom() == null && workshop.getTimeslot() != null)
                .penalize("Only one variable null", HardSoftScore.ofHard(10));
    }

    private Constraint workshopNotAvailableConflict(ConstraintFactory constraintFactory) {
        return constraintFactory
                .from(Workshop.class)
                .filter(workshop -> workshop.getTimeslot() != null && workshop.getPossibleTimeslots().stream().map(Timeslot::getId).noneMatch(id -> id.equals(workshop.getTimeslot().getId())))
                .penalize("Workshop not available conflict", HardSoftScore.ONE_HARD);
    }

    private Constraint roomConflict(ConstraintFactory constraintFactory) {
        return constraintFactory
                .fromUniquePair(
                        Workshop.class,
                        Joiners.equal(Workshop::getTimeslot),
                        Joiners.equal(Workshop::getRoom)
                )
                .penalize("Room conflict", HardSoftScore.ofHard(5));
    }
}
