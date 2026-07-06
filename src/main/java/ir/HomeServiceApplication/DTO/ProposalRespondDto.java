//package ir.HomeServiceApplication.mapper;
//
//import ir.HomeServiceApplication.DTO.ProposalDto;
//import ir.HomeServiceApplication.entity.Proposal;
//
//public class ProposalMapper {
//
//    public static ProposalRespondDto toDto(Proposal proposal) {
//
//        ProposalRespondDto dto = new ProposalRespondDto();
//
//        dto.setId(proposal.getId());
//        dto.setProposalPrice(proposal.getProposalPrice());
//        dto.setStartDate(proposal.getStartDate());
//        dto.setDuration(proposal.getDuration());
//
//        dto.setSpecialistId(proposal.getSpecialist().getId());
//        dto.setOrderId(proposal.getOrder().getId());
//
//        //
//        dto.setSpecialistName(proposal.getSpecialist().getName());
//
//        return dto;
//    }
//}