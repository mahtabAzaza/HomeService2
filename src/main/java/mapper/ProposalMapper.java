package mapper;

import DTO.ProposalDto;
import entity.Proposal;

public class ProposalMapper {

    public static ProposalDto toDto(Proposal proposal) {
        ProposalDto dto = new ProposalDto();
        dto.setId(proposal.getId());
        dto.setProposalPrice(proposal.getProposalPrice());
        dto.setStartDate(proposal.getStartDate());
        dto.setDuration(proposal.getDuration());
        dto.setSpecialistId(proposal.getSpecialist().getId());
        dto.setOrderId(proposal.getOrder().getId());
        return dto;
    }
}
