package com.syncrotess.openfriday.core;

import com.syncrotess.openfriday.entities.Timeslot;
import com.syncrotess.openfriday.entities.Vote;
import com.syncrotess.openfriday.entities.Workshop;
import org.optaplanner.core.api.score.buildin.hardmediumsoft.HardMediumSoftScore;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.ConstraintFactory;
import org.optaplanner.core.api.score.stream.ConstraintProvider;
import org.optaplanner.core.api.score.stream.Joiners;

import java.util.stream.Collectors;

public class TimetableConstraintProvider implements ConstraintProvider {
    @Override
    public Constraint[] defineConstraints(ConstraintFactory constraintFactory) {
        return new Constraint[] {
            roomConflict(constraintFactory),
            roomNotAvailableConflict(constraintFactory),
            workshopNotAvailableConflict(constraintFactory),
            onlyOneVariableNullConflict(constraintFactory),
            workshopNotUsedPunishment(constraintFactory),
            votesReward(constraintFactory),
            userTimeConflict(constraintFactory)
        };
    }

    private Constraint userTimeConflict(ConstraintFactory constraintFactory) {
        return constraintFactory
                .from(Workshop.class)
                .join(Vote.class)
                .filter((workshop, vote) -> vote.getWorkshop().getId().equals(workshop.getId())
                                            && workshop.getTimeslot() != null && !vote.getUser().getTimeslots().contains(workshop.getTimeslot()))
                .penalize("User time conflict", HardMediumSoftScore.ONE_SOFT);
    }

    private Constraint votesReward(ConstraintFactory constraintFactory) {
        return constraintFactory
                .from(Workshop.class)
                .filter(workshop -> workshop.getRoom() != null
                                    && workshop.getTimeslot() != null)
                .reward("Reward votes", HardMediumSoftScore.ONE_MEDIUM, Workshop::getVotesHigh);
    }

    private Constraint roomNotAvailableConflict(ConstraintFactory constraintFactory) {
        return constraintFactory
                .from(Workshop.class)
                .filter(workshop -> workshop.getRoom() != null
                                    && workshop.getTimeslot() != null
                                    // && !workshop.getRoom().getTimeslots().stream().map(Timeslot::getId).collect(Collectors.toList()).contains(workshop.getTimeslot().getId()))
                                    && !workshop.getRoom().getTimeslots().contains(workshop.getTimeslot()))
                .penalize("Room not available conflict", HardMediumSoftScore.ofHard(5));
    }

    private Constraint workshopNotUsedPunishment(ConstraintFactory constraintFactory) {
        return constraintFactory
                .from(Workshop.class)
                .filter(workshop -> workshop.getTimeslot() == null && workshop.getRoom() == null)
                .penalize("Workshop not used", HardMediumSoftScore.ONE_MEDIUM);
    }

    private Constraint onlyOneVariableNullConflict(ConstraintFactory constraintFactory) {
        return constraintFactory
                .from(Workshop.class)
                .filter(workshop -> workshop.getRoom() != null && workshop.getTimeslot() == null || workshop.getRoom() == null && workshop.getTimeslot() != null)
                .penalize("Only one variable null", HardMediumSoftScore.ofHard(10));
    }

    private Constraint workshopNotAvailableConflict(ConstraintFactory constraintFactory) {
        return constraintFactory
                .from(Workshop.class)
                .filter(workshop -> workshop.getTimeslot() != null && workshop.getPossibleTimeslots().stream().map(Timeslot::getId).noneMatch(id -> id.equals(workshop.getTimeslot().getId())))
                .penalize("Workshop not available conflict", HardMediumSoftScore.ONE_HARD);
    }

    private Constraint roomConflict(ConstraintFactory constraintFactory) {
        return constraintFactory
                .fromUniquePair(
                        Workshop.class,
                        Joiners.equal(Workshop::getTimeslot),
                        Joiners.equal(Workshop::getRoom)
                )
                .filter((workshop, workshop2) -> workshop.getRoom() != null && workshop.getTimeslot() != null)  // excludes pairs of unused workshops
                .penalize("Room conflict", HardMediumSoftScore.ofHard(5));
    }
}
