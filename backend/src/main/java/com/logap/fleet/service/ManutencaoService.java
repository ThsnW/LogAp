package com.logap.fleet.service;

import com.logap.fleet.dto.ManutencaoDTO;
import com.logap.fleet.entity.Manutencao;
import com.logap.fleet.entity.Veiculo;
import com.logap.fleet.repository.ManutencaoRepository;
import com.logap.fleet.repository.VeiculoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ManutencaoService {

    private final ManutencaoRepository manutencaoRepository;
    private final VeiculoRepository veiculoRepository;

    public List<ManutencaoDTO> listarComFiltros(String placa, String modelo, String tipo, java.time.LocalDate dataInicio, java.time.LocalDate dataFim) {
        return manutencaoRepository.findWithFilters(placa, modelo, tipo, dataInicio, dataFim).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public ManutencaoDTO buscarPorId(Long id) {
        Manutencao manutencao = manutencaoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Manutenção não encontrada com ID: " + id));
        return toDTO(manutencao);
    }

    @Transactional
    public ManutencaoDTO criar(ManutencaoDTO dto) {
        Veiculo veiculo = veiculoRepository.findById(dto.getVeiculoId())
                .orElseThrow(() -> new RuntimeException("Veículo não encontrado com ID: " + dto.getVeiculoId()));

        validarDatas(dto);

        Manutencao manutencao = Manutencao.builder()
                .veiculo(veiculo)
                .dataInicio(dto.getDataInicio())
                .dataFinalizacao(dto.getDataFinalizacao())
                .tipoServico(dto.getTipoServico())
                .custoEstimado(dto.getCustoEstimado())
                .status(dto.getStatus() != null ? dto.getStatus() : "PENDENTE")
                .observacoes(dto.getObservacoes())
                .build();

        manutencao = manutencaoRepository.save(manutencao);
        return toDTO(manutencao);
    }

    @Transactional
    public ManutencaoDTO atualizar(Long id, ManutencaoDTO dto) {
        Manutencao manutencao = manutencaoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Manutenção não encontrada com ID: " + id));

        Veiculo veiculo = veiculoRepository.findById(dto.getVeiculoId())
                .orElseThrow(() -> new RuntimeException("Veículo não encontrado com ID: " + dto.getVeiculoId()));

        validarDatas(dto);

        manutencao.setVeiculo(veiculo);
        manutencao.setDataInicio(dto.getDataInicio());
        manutencao.setDataFinalizacao(dto.getDataFinalizacao());
        manutencao.setTipoServico(dto.getTipoServico());
        manutencao.setCustoEstimado(dto.getCustoEstimado());
        manutencao.setStatus(dto.getStatus());
        manutencao.setObservacoes(dto.getObservacoes());

        manutencao = manutencaoRepository.save(manutencao);
        return toDTO(manutencao);
    }

    @Transactional
    public void deletar(Long id) {
        if (!manutencaoRepository.existsById(id)) {
            throw new RuntimeException("Manutenção não encontrada com ID: " + id);
        }
        manutencaoRepository.deleteById(id);
    }

    private void validarDatas(ManutencaoDTO dto) {
        if (dto.getDataFinalizacao() != null && dto.getDataFinalizacao().isBefore(dto.getDataInicio())) {
            throw new RuntimeException("Data de finalização não pode ser anterior à data de início");
        }
    }

    private ManutencaoDTO toDTO(Manutencao m) {
        return ManutencaoDTO.builder()
                .id(m.getId())
                .veiculoId(m.getVeiculo().getId())
                .veiculoPlaca(m.getVeiculo().getPlaca())
                .veiculoModelo(m.getVeiculo().getModelo())
                .dataInicio(m.getDataInicio())
                .dataFinalizacao(m.getDataFinalizacao())
                .tipoServico(m.getTipoServico())
                .custoEstimado(m.getCustoEstimado())
                .status(m.getStatus())
                .observacoes(m.getObservacoes())
                .build();
    }
}
