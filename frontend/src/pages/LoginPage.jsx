import { useState } from 'react';
import { useAuth } from '../context/AuthContext';
import { FiTruck, FiMail, FiLock, FiUser, FiEye, FiEyeOff } from 'react-icons/fi';

function LoginPage() {
  const [isLogin, setIsLogin] = useState(true);
  const [email, setEmail] = useState('');
  const [senha, setSenha] = useState('');
  const [showPassword, setShowPassword] = useState(false);
  const [nome, setNome] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const { login, register } = useAuth();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);

    try {
      if (isLogin) {
        await login(email, senha);
      } else {
        await register(nome, email, senha);
      }
    } catch (err) {
      const msg = err.response?.data?.erro || 'Erro ao processar solicitação';
      setError(msg);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="login-page">
      <div className="login-container fade-in">
        <div className="login-card">
          <div className="login-header">
            <div className="login-logo">
              <FiTruck />
            </div>
            <h1>LogAp Fleet</h1>
            <p>{isLogin ? 'Acesse sua conta' : 'Crie sua conta'}</p>
          </div>

          {error && <div className="login-error">{error}</div>}

          <form onSubmit={handleSubmit}>
            {!isLogin && (
              <div className="form-group">
                <label htmlFor="nome">
                  <FiUser style={{ marginRight: 6, verticalAlign: 'middle' }} />
                  Nome
                </label>
                <input
                  id="nome"
                  type="text"
                  className="form-control"
                  placeholder="Seu nome completo"
                  value={nome}
                  onChange={(e) => setNome(e.target.value)}
                  required={!isLogin}
                />
              </div>
            )}

            <div className="form-group">
              <label htmlFor="email">
                <FiMail style={{ marginRight: 6, verticalAlign: 'middle' }} />
                Email
              </label>
              <input
                id="email"
                type="email"
                className="form-control"
                placeholder="seu@email.com"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                required
              />
            </div>

            <div className="form-group">
              <label htmlFor="senha">
                <FiLock style={{ marginRight: 6, verticalAlign: 'middle' }} />
                Senha
              </label>
              <div style={{ position: 'relative' }}>
                <input
                  id="senha"
                  type={showPassword ? "text" : "password"}
                  className="form-control"
                  style={{ paddingRight: '40px' }}
                  placeholder="••••••••"
                  value={senha}
                  onChange={(e) => setSenha(e.target.value)}
                  required
                />
                <button
                  type="button"
                  onClick={() => setShowPassword(!showPassword)}
                  style={{
                    position: 'absolute',
                    right: '12px',
                    top: '50%',
                    transform: 'translateY(-50%)',
                    background: 'none',
                    border: 'none',
                    color: '#9ca3af',
                    cursor: 'pointer',
                    display: 'flex',
                    alignItems: 'center',
                    padding: 0
                  }}
                  title={showPassword ? "Ocultar senha" : "Mostrar senha"}
                >
                  {showPassword ? <FiEyeOff size={18} /> : <FiEye size={18} />}
                </button>
              </div>
            </div>

            <button
              type="submit"
              className="btn btn-primary login-btn"
              disabled={loading}
            >
              {loading ? 'Processando...' : isLogin ? 'Entrar' : 'Criar Conta'}
            </button>
          </form>

          <div className="login-footer">
            {isLogin ? (
              <p>
                Não tem conta?{' '}
                <a href="#" onClick={(e) => { e.preventDefault(); setIsLogin(false); setError(''); }}>
                  Registre-se
                </a>
              </p>
            ) : (
              <p>
                Já tem conta?{' '}
                <a href="#" onClick={(e) => { e.preventDefault(); setIsLogin(true); setError(''); }}>
                  Faça login
                </a>
              </p>
            )}
          </div>
        </div>
      </div>
    </div>
  );
}

export default LoginPage;
