package com.syncrotess.openfriday.core;

import com.syncrotess.openfriday.entities.Timeslot;
import com.syncrotess.openfriday.entities.Vote;
import com.syncrotess.openfriday.entities.Workshop;
import org.optaplanner.core.api.score.buildin.hardmediumsoft.HardMediumSoftScore;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.ConstraintFactory;
import org.optaplanner.core.api.score.stream.ConstraintProvider;
import org.optaplanner.core.api.score.stream.Joiners;


// used by optaplanner to calculate costs of a solution
public class TimetableConstraintProvider implements ConstraintProvider {
    @Override
    public Constraint[] defineConstraints(ConstraintFactory constraintFactory) {
        return new Constraint[] {
            roomConflict(constraintFactory),
            roomNotAvailableConflict(constraintFactory),
            workshopNotAvailableConflict(constraintFactory),
            onlyOneVariableNullConflict(constraintFactory),
            roomTooSmallConflict(constraintFactory),
            workshopNotUsedPunishment(constraintFactory),
            votesReward(constraintFactory),
            userAvailabilityTimeConflict(constraintFactory),
            userVoteTimeConflict(constraintFactory)
        };
    }

    private Constraint roomTooSmallConflict(ConstraintFactory constraintFactory) {
        return constraintFactory
                .from(Workshop.class)
                .filter(workshop -> workshop.getRoom() != null
                                    && workshop.getVotesHigh() + workshop.getVotesLow() / 2.0 > workshop.getRoom().getSize())
                .penalize("Room too small", HardMediumSoftScore.ONE_MEDIUM, this::getOversize);
    }

    // Penalize time conflicts between the slot a workshop takes place at and the availability of the users who voted for this workshop
    // TODO currently only high priority votes are penalized. This should be tested with realistic data and may be adjusted
    private Constraint userAvailabilityTimeConflict(ConstraintFactory constraintFactory) {
        return constraintFactory
                .from(Workshop.class)
                .join(Vote.class)
                .filter((workshop, vote) -> vote.getWorkshop().getId().equals(workshop.getId()) && vote.getPriority() == 2
                                            && workshop.getTimeslot() != null && !vote.getUser().getTimeslots().contains(workshop.getTimeslot()))
                .penalize("User availability time conflict", HardMediumSoftScore.ONE_SOFT);
    }

    // Penalize time conflicts between workshops taking place at the same time when one user wants to participate at both
    private Constraint userVoteTimeConflict(ConstraintFactory constraintFactory) {
        return constraintFactory
                .from(Vote.class)
                .join(Vote.class, Joiners.equal(Vote::getUser))
                .filter((vote, vote2) -> vote.getPriority() == 2 && vote2.getPriority() == 2 &&
                                        vote.getWorkshop().getTimeslot() != null && vote2.getWorkshop().getTimeslot() != null
                                        && vote.getWorkshop().getTimeslot().getId().equals(vote2.getWorkshop().getTimeslot().getId()))
                .penalize("User vote time conflict", HardMediumSoftScore.ONE_SOFT);
    }

    // Reward workshops with many votes picked for planning
    private Constraint votesReward(ConstraintFactory constraintFactory) {
        return constraintFactory
                .from(Workshop.class)
                .filter(workshop -> workshop.getRoom() != null
                                    && workshop.getTimeslot() != null)
                .reward("Reward votes", HardMediumSoftScore.ONE_MEDIUM, this::getWeightedVotes);
    }

    // Prevent workshops being put in a room slot where the room isn't available
    private Constraint roomNotAvailableConflict(ConstraintFactory constraintFactory) {
        return constraintFactory
                .from(Workshop.class)
                .filter(workshop -> workshop.getRoom() != null
                                    && workshop.getTimeslot() != null
                                    // && !workshop.getRoom().getTimeslots().stream().map(Timeslot::getId).collect(Collectors.toList()).contains(workshop.getTimeslot().getId()))
                                    && !workshop.getRoom().getTimeslots().contains(workshop.getTimeslot()))
                .penalize("Room not available conflict", HardMediumSoftScore.ofHard(5));
    }

    // Punish unused workshops to be sure all available slots get filled
    private Constraint workshopNotUsedPunishment(ConstraintFactory constraintFactory) {
        return constraintFactory
                .from(Workshop.class)
                .filter(workshop -> workshop.getTimeslot() == null && workshop.getRoom() == null)
                .penalize("Workshop not used", HardMediumSoftScore.ONE_MEDIUM, this::getWeightedVotes);
    }

    // Prevent algorithm from assigning only a room to a workshop, but no timeslot, and vice versa
    private Constraint onlyOneVariableNullConflict(ConstraintFactory constraintFactory) {
        return constraintFactory
                .from(Workshop.class)
                .filter(workshop -> workshop.getRoom() != null && workshop.getTimeslot() == null || workshop.getRoom() == null && workshop.getTimeslot() != null)
                .penalize("Only one variable null", HardMediumSoftScore.ofHard(10));
    }

    // Prevent a workshop from being assigned to a timeslot where the workshop can't be held
    private Constraint workshopNotAvailableConflict(ConstraintFactory constraintFactory) {
        return constraintFactory
                .from(Workshop.class)
                .filter(workshop -> workshop.getTimeslot() != null && workshop.getPossibleTimeslots().stream().map(Timeslot::getId).noneMatch(id -> id.equals(workshop.getTimeslot().getId())))
                .penalize("Workshop not available conflict", HardMediumSoftScore.ONE_HARD);
    }

    // Prevent multiple workshops being assigned to the same room AND the same timeslot
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

    // Returns weighted votes of the given workshop. Votes with high priority are counted twice
    private int getWeightedVotes(Workshop workshop) {
        return workshop.getVotesHigh() * 2 + workshop.getVotesLow();
    }

    private int getOversize(Workshop workshop) {
        int oversize = (int) Math.ceil((workshop.getVotesHigh() + workshop.getVotesLow() / 2.0) - workshop.getRoom().getSize());
        return Math.max(oversize, 0);
    }
}
