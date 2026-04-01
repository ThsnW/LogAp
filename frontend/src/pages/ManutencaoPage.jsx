import { useState, useEffect } from 'react';
import { manutencaoApi, veiculoApi } from '../services/api';
import { toast } from 'react-toastify';
import { FiPlus, FiEdit2, FiTrash2, FiTool, FiX } from 'react-icons/fi';

const SERVICE_TYPES = [
  { value: 'TROCA_PNEUS', label: 'Troca de Pneus' },
  { value: 'MOTOR', label: 'Motor' },
  { value: 'OLEO', label: 'Óleo' },
  { value: 'FREIOS', label: 'Freios' },
  { value: 'SUSPENSAO', label: 'Suspensão' },
  { value: 'ELETRICA', label: 'Elétrica' },
  { value: 'REVISAO_GERAL', label: 'Revisão Geral' },
  { value: 'OUTROS', label: 'Outros' },
];

const STATUS_OPTIONS = [
  { value: 'PENDENTE', label: 'Pendente' },
  { value: 'EM_REALIZACAO', label: 'Em Realização' },
  { value: 'CONCLUIDA', label: 'Concluída' },
];

function formatCurrency(value) {
  return new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(value);
}

function formatDate(dateStr) {
  if (!dateStr) return '';
  const d = new Date(dateStr + 'T00:00:00');
  return d.toLocaleDateString('pt-BR');
}

function getStatusClass(status) {
  switch (status) {
    case 'PENDENTE': return 'badge-pendente';
    case 'EM_REALIZACAO': return 'badge-em-realizacao';
    case 'CONCLUIDA': return 'badge-concluida';
    default: return '';
  }
}

function getStatusLabel(status) {
  const opt = STATUS_OPTIONS.find(s => s.value === status);
  return opt ? opt.label : status;
}

function getServiceLabel(type) {
  const opt = SERVICE_TYPES.find(s => s.value === type);
  return opt ? opt.label : type;
}

const emptyForm = {
  veiculoId: '',
  dataInicio: '',
  dataFinalizacao: '',
  tipoServico: '',
  custoEstimado: '',
  status: 'PENDENTE',
  observacoes: '',
};

function ManutencaoPage() {
  const [manutencoes, setManutencoes] = useState([]);
  const [veiculos, setVeiculos] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showModal, setShowModal] = useState(false);
  const [editingId, setEditingId] = useState(null);
  const [viewingManutencao, setViewingManutencao] = useState(null);
  const [editingStatusId, setEditingStatusId] = useState(null);
  const [form, setForm] = useState({ ...emptyForm });
  const [submitting, setSubmitting] = useState(false);
  const [deleteConfirm, setDeleteConfirm] = useState(null);

  // Filters state
  const [filterPlaca, setFilterPlaca] = useState('');
  const [filterModelo, setFilterModelo] = useState('');
  const [filterTipo, setFilterTipo] = useState('');
  const [filterDateStart, setFilterDateStart] = useState('');
  const [filterDateEnd, setFilterDateEnd] = useState('');

  useEffect(() => {
    fetchData();
  }, []);

  const fetchData = async () => {
    try {
      const params = {};
      if (filterPlaca) params.placa = filterPlaca;
      if (filterModelo) params.modelo = filterModelo;
      if (filterTipo) params.tipo = filterTipo;
      if (filterDateStart) params.dataInicio = filterDateStart;
      if (filterDateEnd) params.dataFim = filterDateEnd;

      const [mRes, vRes] = await Promise.all([
        manutencaoApi.listar(params),
        veiculoApi.listar(),
      ]);
      setManutencoes(mRes.data);
      setVeiculos(vRes.data);
    } catch (err) {
      toast.error('Erro ao carregar dados');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const openCreateModal = () => {
    setEditingId(null);
    setForm({ ...emptyForm });
    setShowModal(true);
  };

  const openEditModal = (m) => {
    setEditingId(m.id);
    setForm({
      veiculoId: m.veiculoId?.toString() || '',
      dataInicio: m.dataInicio || '',
      dataFinalizacao: m.dataFinalizacao || '',
      tipoServico: m.tipoServico || '',
      custoEstimado: m.custoEstimado?.toString() || '',
      status: m.status || 'PENDENTE',
      observacoes: m.observacoes || '',
    });
    setShowModal(true);
  };

  const closeModal = () => {
    setShowModal(false);
    setEditingId(null);
    setForm({ ...emptyForm });
  };

  const handleChange = (e) => {
    const { name, value } = e.target;
    setForm(prev => ({ ...prev, [name]: value }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setSubmitting(true);

    const payload = {
      veiculoId: parseInt(form.veiculoId),
      dataInicio: form.dataInicio,
      dataFinalizacao: form.dataFinalizacao,
      tipoServico: form.tipoServico,
      custoEstimado: parseFloat(form.custoEstimado),
      status: form.status,
      observacoes: form.observacoes || null,
    };

    try {
      if (editingId) {
        await manutencaoApi.atualizar(editingId, payload);
        toast.success('Manutenção atualizada com sucesso!');
      } else {
        await manutencaoApi.criar(payload);
        toast.success('Manutenção criada com sucesso!');
      }
      closeModal();
      fetchData();
    } catch (err) {
      const msg = err.response?.data?.erro || 'Erro ao salvar manutenção';
      toast.error(msg);
    } finally {
      setSubmitting(false);
    }
  };

  const handleDelete = async (id) => {
    try {
      await manutencaoApi.deletar(id);
      toast.success('Manutenção removida com sucesso!');
      setDeleteConfirm(null);
      fetchData();
    } catch (err) {
      toast.error('Erro ao remover manutenção');
    }
  };

  const handleStatusChange = async (m, newStatus) => {
    setEditingStatusId(null);
    if (m.status === newStatus) return;

    try {
      const payload = {
        veiculoId: m.veiculoId,
        dataInicio: m.dataInicio,
        dataFinalizacao: m.dataFinalizacao,
        tipoServico: m.tipoServico,
        custoEstimado: m.custoEstimado,
        status: newStatus,
        observacoes: m.observacoes || null,
      };
      await manutencaoApi.atualizar(m.id, payload);
      toast.success('Status atualizado com sucesso!');
      
      // Update local state without full refetch for snappier UI
      setManutencoes(prev => prev.map(item => item.id === m.id ? { ...item, status: newStatus } : item));
    } catch (err) {
      toast.error('Erro ao atualizar status');
    }
  };

  if (loading) {
    return (
      <div className="loading-spinner">
        <div className="spinner"></div>
      </div>
    );
  }

  const uniquePlacas = [...new Set(veiculos.map(v => v.placa))].filter(Boolean);
  const uniqueModelos = [...new Set(veiculos.map(v => v.modelo))].filter(Boolean);
  const uniqueTipos = [...new Set(veiculos.map(v => v.tipo))].filter(Boolean);

  return (
    <div className="fade-in">
      <div className="page-header">
        <div>
          <h1>Agendamento de Manutenção</h1>
          <p>Gerencie as manutenções da frota</p>
        </div>
        <button className="btn btn-primary" onClick={openCreateModal} id="btn-new-maintenance">
          <FiPlus /> Nova Manutenção
        </button>
      </div>

      {(manutencoes.length > 0 || filterPlaca || filterModelo || filterTipo || filterDateStart || filterDateEnd) && (
        <div className="card" style={{ marginBottom: '20px', padding: '16px', display: 'flex', gap: '16px', flexWrap: 'wrap', alignItems: 'flex-end', background: '#1a1f35' }}>
          <div style={{ flex: '1 1 120px' }}>
            <label style={{ display: 'block', marginBottom: '8px', color: '#94a3b8', fontSize: '0.85rem', fontWeight: 500 }}>Placa</label>
            <select className="form-control" value={filterPlaca} onChange={e => setFilterPlaca(e.target.value)}>
              <option value="">Todas</option>
              {uniquePlacas.map(p => <option key={p} value={p}>{p}</option>)}
            </select>
          </div>
          <div style={{ flex: '1 1 120px' }}>
            <label style={{ display: 'block', marginBottom: '8px', color: '#94a3b8', fontSize: '0.85rem', fontWeight: 500 }}>Modelo</label>
            <select className="form-control" value={filterModelo} onChange={e => setFilterModelo(e.target.value)}>
              <option value="">Todos</option>
              {uniqueModelos.map(m => <option key={m} value={m}>{m}</option>)}
            </select>
          </div>
          <div style={{ flex: '1 1 120px' }}>
            <label style={{ display: 'block', marginBottom: '8px', color: '#94a3b8', fontSize: '0.85rem', fontWeight: 500 }}>Tipo</label>
            <select className="form-control" value={filterTipo} onChange={e => setFilterTipo(e.target.value)}>
              <option value="">Todos</option>
              {uniqueTipos.map(t => <option key={t} value={t}>{t === 'LEVE' ? 'Leve' : 'Pesado'}</option>)}
            </select>
          </div>
          
          <div style={{ flex: '0 1 150px' }}>
            <label style={{ display: 'block', marginBottom: '8px', color: '#94a3b8', fontSize: '0.85rem', fontWeight: 500 }}>Manutenção a partir de</label>
            <input 
              type="date" 
              className="form-control" 
              value={filterDateStart}
              onChange={e => setFilterDateStart(e.target.value)}
            />
          </div>
          <div style={{ flex: '0 1 150px' }}>
            <label style={{ display: 'block', marginBottom: '8px', color: '#94a3b8', fontSize: '0.85rem', fontWeight: 500 }}>Até</label>
            <input 
              type="date" 
              className="form-control" 
              value={filterDateEnd}
              onChange={e => setFilterDateEnd(e.target.value)}
            />
          </div>
          <div style={{ display: 'flex', gap: '8px' }}>
            <button className="btn btn-primary" onClick={() => fetchData()} style={{ height: '42px', display: 'flex', alignItems: 'center' }}>
              Buscar
            </button>
            <button className="btn btn-secondary" onClick={() => {
              setFilterPlaca('');
              setFilterModelo('');
              setFilterTipo('');
              setFilterDateStart('');
              setFilterDateEnd('');
              manutencaoApi.listar({}).then(res => setManutencoes(res.data));
            }} style={{ height: '42px', display: 'flex', alignItems: 'center' }}>
              Limpar 
            </button>
          </div>
        </div>
      )}

      {/* Table */}
      {manutencoes.length > 0 ? (
        <div className="table-container">
          <table>
            <thead>
              <tr>
                <th>Veículo</th>
                <th>Serviço</th>
                <th>Data Início</th>
                <th>Data Finalização</th>
                <th>Custo Estimado</th>
                <th>Status</th>
                <th>Ações</th>
              </tr>
            </thead>
            <tbody>
              {manutencoes.length > 0 ? manutencoes.map((m) => (
                <tr key={m.id} onClick={() => setViewingManutencao(m)} style={{ cursor: 'pointer' }} className="table-row-hover">
                  <td>
                    <strong style={{ color: '#f1f5f9' }}>{m.veiculoPlaca}</strong>
                    <br />
                    <small style={{ color: '#64748b' }}>{m.veiculoModelo}</small>
                  </td>
                  <td>{getServiceLabel(m.tipoServico)}</td>
                  <td>{formatDate(m.dataInicio)}</td>
                  <td>{formatDate(m.dataFinalizacao)}</td>
                  <td style={{ fontWeight: 600, color: '#f59e0b' }}>
                    {formatCurrency(m.custoEstimado)}
                  </td>
                  <td onClick={(e) => e.stopPropagation()}>
                    {editingStatusId === m.id ? (
                      <select
                        className="form-control"
                        autoFocus
                        value={m.status}
                        onChange={(e) => handleStatusChange(m, e.target.value)}
                        onBlur={() => setEditingStatusId(null)}
                        style={{ padding: '4px 8px', fontSize: '0.85rem', width: '130px', height: 'auto' }}
                      >
                        {STATUS_OPTIONS.map(s => <option key={s.value} value={s.value}>{s.label}</option>)}
                      </select>
                    ) : (
                      <span 
                        className={`badge ${getStatusClass(m.status)}`}
                        onClick={() => setEditingStatusId(m.id)}
                        title="Clique para alterar o status"
                        style={{ cursor: 'pointer' }}
                      >
                        {getStatusLabel(m.status)}
                      </span>
                    )}
                  </td>
                  <td>
                    <div className="table-actions">
                      <button
                        className="btn-icon"
                        onClick={(e) => { e.stopPropagation(); openEditModal(m); }}
                        title="Editar"
                      >
                        <FiEdit2 />
                      </button>
                      {deleteConfirm === m.id ? (
                        <>
                          <button
                            className="btn btn-danger btn-sm"
                            onClick={(e) => { e.stopPropagation(); handleDelete(m.id); }}
                          >
                            Confirmar
                          </button>
                          <button
                            className="btn btn-secondary btn-sm"
                            onClick={(e) => { e.stopPropagation(); setDeleteConfirm(null); }}
                          >
                            Cancelar
                          </button>
                        </>
                      ) : (
                        <button
                          className="btn-icon danger"
                          onClick={(e) => { e.stopPropagation(); setDeleteConfirm(m.id); }}
                          title="Excluir"
                        >
                          <FiTrash2 />
                        </button>
                      )}
                    </div>
                  </td>
                </tr>
              )) : (
                <tr>
                  <td colSpan="7" style={{ textAlign: 'center', padding: '30px', color: '#64748b' }}>
                    Nenhuma manutenção encontrada para estes filtros.
                  </td>
                </tr>
              )}
            </tbody>
          </table>
        </div>
      ) : (
        <div className="card empty-state">
          <FiTool />
          <p>
            {(filterPlaca || filterModelo || filterTipo || filterDateStart || filterDateEnd) 
              ? 'Nenhuma manutenção encontrada para estes filtros.' 
              : 'Nenhuma manutenção registrada'}
          </p>
          {!(filterPlaca || filterModelo || filterTipo || filterDateStart || filterDateEnd) && (
            <button className="btn btn-primary" onClick={openCreateModal}>
              <FiPlus /> Agendar Manutenção
            </button>
          )}
        </div>
      )}

      {/* View Details Modal */}
      {viewingManutencao && (
        <div className="modal-overlay" onClick={(e) => e.target.className === 'modal-overlay' && setViewingManutencao(null)}>
          <div className="modal" style={{ maxWidth: '600px' }}>
            <div className="modal-header">
              <h2>Detalhes da Manutenção</h2>
              <button className="btn-icon" onClick={() => setViewingManutencao(null)}>
                <FiX />
              </button>
            </div>
            <div className="modal-body" style={{ display: 'flex', flexDirection: 'column', gap: '20px' }}>
              <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '20px' }}>
                <div style={{ background: '#1a1f35', padding: '12px', borderRadius: '8px' }}>
                  <small style={{ color: '#94a3b8', display: 'block', marginBottom: '4px' }}>Veículo</small>
                  <div style={{ fontWeight: 600, fontSize: '1.05rem', color: '#f8fafc' }}>
                    {viewingManutencao.veiculoPlaca} - {viewingManutencao.veiculoModelo}
                  </div>
                </div>
                <div style={{ background: '#1a1f35', padding: '12px', borderRadius: '8px' }}>
                  <small style={{ color: '#94a3b8', display: 'block', marginBottom: '4px' }}>Status Atual</small>
                  <div>
                    <span className={`badge ${getStatusClass(viewingManutencao.status)}`}>
                      {getStatusLabel(viewingManutencao.status)}
                    </span>
                  </div>
                </div>
                <div style={{ background: '#1a1f35', padding: '12px', borderRadius: '8px' }}>
                  <small style={{ color: '#94a3b8', display: 'block', marginBottom: '4px' }}>Serviço Realizado</small>
                  <div style={{ fontWeight: 500, color: '#f8fafc' }}>{getServiceLabel(viewingManutencao.tipoServico)}</div>
                </div>
                <div style={{ background: '#1a1f35', padding: '12px', borderRadius: '8px' }}>
                  <small style={{ color: '#94a3b8', display: 'block', marginBottom: '4px' }}>Custo Estimado</small>
                  <div style={{ fontWeight: 600, color: '#f59e0b', fontSize: '1.2rem' }}>
                    {formatCurrency(viewingManutencao.custoEstimado)}
                  </div>
                </div>
                <div style={{ background: '#1a1f35', padding: '12px', borderRadius: '8px' }}>
                  <small style={{ color: '#94a3b8', display: 'block', marginBottom: '4px' }}>Data Inicial</small>
                  <div style={{ color: '#f8fafc' }}>{formatDate(viewingManutencao.dataInicio)}</div>
                </div>
                <div style={{ background: '#1a1f35', padding: '12px', borderRadius: '8px' }}>
                  <small style={{ color: '#94a3b8', display: 'block', marginBottom: '4px' }}>Data Finalização</small>
                  <div style={{ color: '#f8fafc' }}>{formatDate(viewingManutencao.dataFinalizacao) || 'Não informada'}</div>
                </div>
              </div>
              <div style={{ padding: '12px', background: '#1a1f35', borderRadius: '8px' }}>
                <small style={{ color: '#94a3b8', display: 'block', marginBottom: '8px' }}>Observações</small>
                <div style={{ color: '#cbd5e1', lineHeight: '1.5' }}>
                  {viewingManutencao.observacoes ? viewingManutencao.observacoes : <span style={{ fontStyle: 'italic', opacity: 0.6 }}>Nenhuma observação registrada para este agendamento.</span>}
                </div>
              </div>
            </div>
            <div className="modal-footer">
              <button className="btn btn-secondary" onClick={() => setViewingManutencao(null)}>
                Fechar
              </button>
              <button className="btn btn-primary" onClick={() => {
                const m = viewingManutencao;
                setViewingManutencao(null);
                openEditModal(m);
              }}>
                <FiEdit2 style={{ marginRight: '6px' }} /> Editar Dados
              </button>
            </div>
          </div>
        </div>
      )}

      {/* Create/Edit Modal */}
      {showModal && (
        <div className="modal-overlay" onClick={(e) => e.target.className === 'modal-overlay' && closeModal()}>
          <div className="modal">
            <div className="modal-header">
              <h2>{editingId ? 'Editar Manutenção' : 'Nova Manutenção'}</h2>
              <button className="btn-icon" onClick={closeModal}>
                <FiX />
              </button>
            </div>

            <form onSubmit={handleSubmit}>
              <div className="modal-body">
                <div className="form-group">
                  <label htmlFor="veiculoId">Veículo *</label>
                  <select
                    id="veiculoId"
                    name="veiculoId"
                    className="form-control"
                    value={form.veiculoId}
                    onChange={handleChange}
                    required
                  >
                    <option value="">Selecione um veículo</option>
                    {veiculos.map((v) => (
                      <option key={v.id} value={v.id}>
                        {v.placa} — {v.tipo} {v.modelo} ({v.ano})
                      </option>
                    ))}
                  </select>
                </div>

                <div className="form-row">
                  <div className="form-group">
                    <label htmlFor="dataInicio">Data de Início *</label>
                    <input
                      id="dataInicio"
                      name="dataInicio"
                      type="date"
                      className="form-control"
                      value={form.dataInicio}
                      onChange={handleChange}
                      required
                    />
                  </div>
                  <div className="form-group">
                    <label htmlFor="dataFinalizacao">Data Finalização</label>
                    <input
                      id="dataFinalizacao"
                      name="dataFinalizacao"
                      type="date"
                      className="form-control"
                      value={form.dataFinalizacao}
                      onChange={handleChange}
                    />
                  </div>
                </div>

                <div className="form-row">
                  <div className="form-group">
                    <label htmlFor="tipoServico">Tipo de Serviço *</label>
                    <select
                      id="tipoServico"
                      name="tipoServico"
                      className="form-control"
                      value={form.tipoServico}
                      onChange={handleChange}
                      required
                    >
                      <option value="">Selecione o tipo</option>
                      {SERVICE_TYPES.map((s) => (
                        <option key={s.value} value={s.value}>{s.label}</option>
                      ))}
                    </select>
                  </div>
                  <div className="form-group">
                    <label htmlFor="custoEstimado">Custo Estimado (R$) *</label>
                    <input
                      id="custoEstimado"
                      name="custoEstimado"
                      type="number"
                      step="0.01"
                      min="0.01"
                      className="form-control"
                      placeholder="0,00"
                      value={form.custoEstimado}
                      onChange={handleChange}
                      required
                    />
                  </div>
                </div>

                <div className="form-group">
                  <label htmlFor="status">Status *</label>
                  <select
                    id="status"
                    name="status"
                    className="form-control"
                    value={form.status}
                    onChange={handleChange}
                    required
                  >
                    {STATUS_OPTIONS.map((s) => (
                      <option key={s.value} value={s.value}>{s.label}</option>
                    ))}
                  </select>
                </div>

                <div className="form-group">
                  <label htmlFor="observacoes">Observações</label>
                  <textarea
                    id="observacoes"
                    name="observacoes"
                    className="form-control"
                    placeholder="Observações adicionais..."
                    value={form.observacoes}
                    onChange={handleChange}
                    rows={3}
                    maxLength={500}
                  />
                </div>
              </div>

              <div className="modal-footer">
                <button type="button" className="btn btn-secondary" onClick={closeModal}>
                  Cancelar
                </button>
                <button type="submit" className="btn btn-primary" disabled={submitting}>
                  {submitting ? 'Salvando...' : editingId ? 'Atualizar' : 'Criar'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* Delete Overlay */}
    </div>
  );
}

export default ManutencaoPage;
