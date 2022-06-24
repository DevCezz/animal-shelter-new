package pl.devcezz.shelter.proposal.application;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import pl.devcezz.shelter.proposal.model.PendingProposal;
import pl.devcezz.shelter.proposal.model.Proposal;
import pl.devcezz.shelter.proposal.model.ProposalId;
import pl.devcezz.shelter.proposal.model.Proposals;
import pl.devcezz.shelter.shared.infrastructure.ProposalTransaction;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;
import static io.vavr.Predicates.instanceOf;
import static pl.devcezz.shelter.proposal.model.ProposalEvent.ProposalAlreadyProcessed.proposalAlreadyProcessedNow;
import static pl.devcezz.shelter.shelter.model.ShelterEvent.ProposalAccepted;

@RequiredArgsConstructor
@ProposalTransaction
public class ShelterOperationsEventHandler {

    private final Proposals proposalRepository;
    private final ApplicationEventPublisher publisher;

    @EventListener
    public void handle(ProposalAccepted event) {
        proposalRepository.findBy(ProposalId.of(event.getProposalId()))
                .map(this::handleProposalAccepted)
                .map(this::saveProposal);
    }

    private Proposal handleProposalAccepted(Proposal proposal) {
        return Match(proposal).of(
                Case($(instanceOf(PendingProposal.class)), PendingProposal::accept),
                Case($(), () -> proposalIsAlreadyProcessed(proposal))
        );
    }

    private Proposal proposalIsAlreadyProcessed(Proposal proposal) {
        publisher.publishEvent(proposalAlreadyProcessedNow(proposal.getProposalId()));
        return proposal;
    }

    private Proposal saveProposal(Proposal proposal) {
        proposalRepository.save(proposal);
        return proposal;
    }
}
