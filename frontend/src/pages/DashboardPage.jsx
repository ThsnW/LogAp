import { useState, useEffect } from 'react';
import { dashboardApi } from '../services/api';
import { FiMapPin, FiTruck, FiCalendar, FiAward, FiDollarSign } from 'react-icons/fi';
import {
  BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer,
  PieChart, Pie, Cell, Legend
} from 'recharts';

const MONTHS_PT = ['Jan', 'Fev', 'Mar', 'Abr', 'Mai', 'Jun', 'Jul', 'Ago', 'Set', 'Out', 'Nov', 'Dez'];
const PIE_COLORS = ['#6366f1', '#f97316'];

const SERVICE_LABELS = {
  TROCA_PNEUS: 'Troca de Pneus',
  MOTOR: 'Motor',
  OLEO: 'Óleo',
  FREIOS: 'Freios',
  SUSPENSAO: 'Suspensão',
  ELETRICA: 'Elétrica',
  REVISAO_GERAL: 'Revisão Geral',
  OUTROS: 'Outros',
};

function formatCurrency(value) {
  return new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(value);
}

function formatKm(value) {
  return new Intl.NumberFormat('pt-BR', { maximumFractionDigits: 0 }).format(value);
}

function formatDate(dateStr) {
  if (!dateStr) return '';
  const d = new Date(dateStr + 'T00:00:00');
  return `${d.getDate().toString().padStart(2, '0')}/${MONTHS_PT[d.getMonth()]}`;
}

function getDateParts(dateStr) {
  if (!dateStr) return { day: '--', month: '---' };
  const d = new Date(dateStr + 'T00:00:00');
  return {
    day: d.getDate().toString().padStart(2, '0'),
    month: MONTHS_PT[d.getMonth()],
  };
}

const CustomTooltip = ({ active, payload }) => {
  if (!active || !payload?.length) return null;
  return (
    <div style={{
      background: '#1a1f35',
      border: '1px solid rgba(99,102,241,0.3)',
      borderRadius: 8,
      padding: '10px 14px',
      fontSize: '0.82rem',
      color: '#f1f5f9'
    }}>
      <p><strong>{payload[0].name || payload[0].payload?.name}</strong></p>
      <p style={{ color: '#818cf8' }}>{formatKm(payload[0].value)} km</p>
    </div>
  );
};

function DashboardPage() {
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchDashboard();
  }, []);

  const fetchDashboard = async () => {
    try {
      const response = await dashboardApi.getData();
      setData(response.data);
    } catch (err) {
      console.error('Erro ao carregar dashboard:', err);
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return (
      <div className="loading-spinner">
        <div className="spinner"></div>
      </div>
    );
  }

  if (!data) {
    return (
      <div className="empty-state">
        <p>Erro ao carregar dados do dashboard</p>
      </div>
    );
  }

  const categoryData = data.volumePorCategoria?.map(v => ({
    name: v.categoria === 'LEVE' ? 'Leve' : 'Pesado',
    value: v.quantidade,
  })) || [];

  const rankingChartData = data.rankingUtilizacao?.slice(0, 5).map(v => ({
    name: v.placa,
    km: parseFloat(v.totalKm),
    modelo: v.modelo,
  })) || [];

  return (
    <div className="fade-in">
      <div className="page-header">
        <div>
          <h1>Dashboard</h1>
          <p>Visão geral da frota em tempo real</p>
        </div>
      </div>

      {/* Metric Cards */}
      <div className="metrics-grid">
        <div className="metric-card purple">
          <div className="metric-icon purple">
            <FiMapPin />
          </div>
          <div className="metric-label">Total KM Frota</div>
          <div className="metric-value">
            {formatKm(data.totalKmFrota)}
            <small>km</small>
          </div>
        </div>

        <div className="metric-card cyan">
          <div className="metric-icon cyan">
            <FiTruck />
          </div>
          <div className="metric-label">Viagens Registradas</div>
          <div className="metric-value">
            {categoryData.reduce((sum, v) => sum + v.value, 0)}
            <small>viagens</small>
          </div>
        </div>

        <div className="metric-card green">
          <div className="metric-icon green">
            <FiCalendar />
          </div>
          <div className="metric-label">Manutenções Agendadas</div>
          <div className="metric-value">
            {data.proximasManutencoes?.length || 0}
            <small>próximas</small>
          </div>
        </div>

        <div className="metric-card orange">
          <div className="metric-icon orange">
            <FiAward />
          </div>
          <div className="metric-label">Veículo Mais Utilizado</div>
          <div className="metric-value" style={{ fontSize: '1.3rem' }}>
            {data.rankingUtilizacao?.[0]?.placa || '--'}
          </div>
        </div>

        <div className="metric-card yellow">
          <div className="metric-icon yellow">
            <FiDollarSign />
          </div>
          <div className="metric-label">Projeção Financeira Mensal</div>
          <div className="metric-value" style={{ fontSize: '1.4rem' }}>
            {formatCurrency(data.projecaoFinanceiraMensal)}
          </div>
        </div>
      </div>

      {/* Charts Row */}
      <div className="charts-grid">
        {/* Ranking Chart */}
        <div className="chart-card">
          <h3>
            <FiAward style={{ marginRight: 8, color: '#6366f1', verticalAlign: 'middle' }} />
            Ranking de Utilização (Top 5)
          </h3>
          <ResponsiveContainer width="100%" height={250}>
            <BarChart data={rankingChartData} layout="vertical" margin={{ left: 20 }}>
              <CartesianGrid strokeDasharray="3 3" stroke="rgba(99,102,241,0.08)" />
              <XAxis type="number" tick={{ fill: '#64748b', fontSize: 12 }} />
              <YAxis type="category" dataKey="name" tick={{ fill: '#94a3b8', fontSize: 12 }} width={80} />
              <Tooltip content={<CustomTooltip />} />
              <Bar dataKey="km" fill="url(#barGradient)" radius={[0, 6, 6, 0]} barSize={20}>
              </Bar>
              <defs>
                <linearGradient id="barGradient" x1="0" y1="0" x2="1" y2="0">
                  <stop offset="0%" stopColor="#6366f1" />
                  <stop offset="100%" stopColor="#a855f7" />
                </linearGradient>
              </defs>
            </BarChart>
          </ResponsiveContainer>
        </div>

        {/* Category Pie Chart */}
        <div className="chart-card">
          <h3>
            <FiTruck style={{ marginRight: 8, color: '#22d3ee', verticalAlign: 'middle' }} />
            Volume por Categoria
          </h3>
          <ResponsiveContainer width="100%" height={250}>
            <PieChart>
              <Pie
                data={categoryData}
                cx="50%"
                cy="50%"
                innerRadius={60}
                outerRadius={90}
                paddingAngle={5}
                dataKey="value"
              >
                {categoryData.map((entry, index) => (
                  <Cell key={index} fill={PIE_COLORS[index % PIE_COLORS.length]} />
                ))}
              </Pie>
              <Tooltip
                formatter={(value, name) => [`${value} viagens`, name]}
                contentStyle={{
                  background: '#1a1f35',
                  border: '1px solid rgba(99,102,241,0.3)',
                  borderRadius: 8,
                  fontSize: '0.82rem',
                  color: '#f1f5f9'
                }}
              />
              <Legend
                wrapperStyle={{ fontSize: '0.85rem', color: '#94a3b8' }}
                iconType="circle"
              />
            </PieChart>
          </ResponsiveContainer>
        </div>
      </div>

      {/* Bottom Row: Schedule + Full Ranking */}
      <div className="charts-grid">
        {/* Maintenance Schedule */}
        <div className="chart-card">
          <h3>
            <FiCalendar style={{ marginRight: 8, color: '#10b981', verticalAlign: 'middle' }} />
            Cronograma de Manutenção
          </h3>
          {data.proximasManutencoes?.length > 0 ? (
            <div className="schedule-list">
              {data.proximasManutencoes.map((m) => {
                const { day, month } = getDateParts(m.dataInicio);
                return (
                  <div key={m.id} className="schedule-item">
                    <div className="schedule-date">
                      <span className="day">{day}</span>
                      <span className="month">{month}</span>
                    </div>
                    <div className="schedule-info">
                      <strong>{SERVICE_LABELS[m.tipoServico] || m.tipoServico}</strong>
                      <span>{m.veiculoPlaca} — {m.veiculoModelo}</span>
                    </div>
                    <div className="schedule-cost">
                      {formatCurrency(m.custoEstimado)}
                    </div>
                  </div>
                );
              })}
            </div>
          ) : (
            <div className="empty-state" style={{ padding: 30 }}>
              <p>Nenhuma manutenção agendada</p>
            </div>
          )}
        </div>

        {/* Full Ranking */}
        <div className="chart-card">
          <h3>
            <FiAward style={{ marginRight: 8, color: '#f59e0b', verticalAlign: 'middle' }} />
            Ranking Completo de Utilização
          </h3>
          <div className="ranking-list">
            {data.rankingUtilizacao?.map((v, i) => (
              <div key={v.id} className="ranking-item">
                <div className={`ranking-position ${i === 0 ? 'gold' : i === 1 ? 'silver' : i === 2 ? 'bronze' : 'default'}`}>
                  {i + 1}º
                </div>
                <div className="ranking-info">
                  <strong>{v.placa}</strong>
                  <span>{v.tipo} {v.modelo}</span>
                </div>
                <div className="ranking-km">
                  {formatKm(v.totalKm)} km
                </div>
              </div>
            ))}
          </div>
        </div>
      </div>
    </div>
  );
}

export default DashboardPage;
