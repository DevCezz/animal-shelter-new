package pl.devcezz.shelter.adoption.proposal.model;

import lombok.NonNull;
import lombok.Value;
import pl.devcezz.shelter.commons.events.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public interface ProposalEvent extends DomainEvent {

    default ProposalId proposalId() {
        return ProposalId.of(getProposalId());
    }

    UUID getProposalId();

    @Value
    class ProposalAlreadyConfirmed implements ProposalEvent {
        @NonNull UUID eventId = UUID.randomUUID();
        @NonNull Instant when;
        @NonNull UUID proposalId;

        public static ProposalAlreadyConfirmed proposalAlreadyConfirmedNow(ProposalId proposalId) {
            return new ProposalAlreadyConfirmed(
                    Instant.now(),
                    proposalId.getValue());
        }
    }

    @Value
    class ProposalAlreadyProcessed implements ProposalEvent {
        @NonNull UUID eventId = UUID.randomUUID();
        @NonNull Instant when;
        @NonNull UUID proposalId;

        public static ProposalAlreadyProcessed proposalAlreadyProcessedNow(ProposalId proposalId) {
            return new ProposalAlreadyProcessed(
                    Instant.now(),
                    proposalId.getValue());
        }
    }
}
