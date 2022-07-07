package pl.devcezz.shelter.adoption.shelter.model

import pl.devcezz.shelter.adoption.proposal.model.ProposalId
import spock.lang.Specification

import static pl.devcezz.shelter.adoption.proposal.model.ProposalFixture.anyProposalId
import static pl.devcezz.shelter.adoption.shelter.model.ShelterEvent.ProposalAccepted
import static pl.devcezz.shelter.adoption.shelter.model.ShelterEvent.ProposalAcceptingFailed
import static pl.devcezz.shelter.adoption.shelter.model.ShelterFixture.shelterWithProposals

class ShelterAcceptingProposalSpec extends Specification {

    def 'shelter should be able to accept up to 10 proposals'() {
        given:
            ProposalId proposalId = anyProposalId()
        and:
            Shelter shelter = shelterWithProposals(proposals)
        when:
            def acceptProposal = shelter.accept(proposalId)
        then:
            acceptProposal.isRight()
            acceptProposal.get().with {
                ProposalAccepted proposalAccepted = it.proposalAccepted
                assert proposalAccepted.proposalId == proposalId.value
            }
        where:
            proposals << (0..9)
    }

    def 'shelter cannot accept more than 10 proposals'() {
        given:
            ProposalId proposalId = anyProposalId()
        and:
            Shelter shelter = shelterWithProposals(proposals)
        when:
            def acceptProposal = shelter.accept(proposalId)
        then:
            acceptProposal.isLeft()
            ProposalAcceptingFailed e = acceptProposal.getLeft()
            e.getReason().contains("no space left for animals in shelter")
        where:
            proposals << [10, 11, 200]
    }
}