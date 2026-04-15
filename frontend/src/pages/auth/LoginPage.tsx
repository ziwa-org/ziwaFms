import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../../contexts/AuthContext';
import { Button } from '../../components/ui/button';
import { Input } from '../../components/ui/input';
import { Checkbox } from '../../components/ui/checkbox';
import { Eye, EyeOff } from 'lucide-react';

export function LoginPage() {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [rememberMe, setRememberMe] = useState(false);
  const [showPassword, setShowPassword] = useState(false);
  const [error, setError] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const { login } = useAuth();
  const navigate = useNavigate();

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');
    setIsLoading(true);

    try {
      await login({ username, password });
      navigate('/dashboard');
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Login failed');
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="min-h-screen flex">
      {/* Left Panel - Login Form */}
      <div className="w-full lg:w-1/2 bg-[#1a2e1a] flex items-center justify-center p-8">
        <div className="w-full max-w-md space-y-8">
          {/* Logo */}
          <div className="mb-12">
            <img src="/ziwa-logo.svg" alt="Ziwa Dairy" className="h-10" />
          </div>

          {/* Heading */}
          <div className="space-y-3">
            <h1 className="text-4xl lg:text-5xl font-bold text-white leading-tight">
              Start Your<br />Day Fresh
            </h1>
            <p className="text-gray-400 text-sm leading-relaxed max-w-sm">
              Manage your deliveries and explore fresh dairy products. 
              Automate orders, update preferences. Enjoy farm-to-door 
              freshness from our eco-conscious local farm.
            </p>
          </div>

          {/* Login Form */}
          <form onSubmit={handleSubmit} className="space-y-5 mt-8">
            <div>
              <Input
                id="username"
                type="text"
                value={username}
                onChange={(e) => setUsername(e.target.value)}
                placeholder="jane@gmail.com"
                className="bg-[#2a3e2a] border-[#3a4e3a] text-white placeholder:text-gray-500 h-12 rounded-lg"
                required
              />
            </div>

            <div className="relative">
              <Input
                id="password"
                type={showPassword ? 'text' : 'password'}
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                placeholder="••••••"
                className="bg-[#2a3e2a] border-[#3a4e3a] text-white placeholder:text-gray-500 h-12 rounded-lg pr-10"
                required
              />
              <button
                type="button"
                onClick={() => setShowPassword(!showPassword)}
                className="absolute right-3 top-1/2 -translate-y-1/2 text-gray-400 hover:text-gray-300"
              >
                {showPassword ? <EyeOff size={18} /> : <Eye size={18} />}
              </button>
            </div>

            {/* Remember Me & Forgot Password */}
            <div className="flex items-center justify-between text-sm">
              <div className="flex items-center space-x-2">
                <Checkbox
                  id="remember"
                  checked={rememberMe}
                  onCheckedChange={(checked) => setRememberMe(checked as boolean)}
                  className="border-gray-500 data-[state=checked]:bg-primary data-[state=checked]:border-primary"
                />
                <label htmlFor="remember" className="text-gray-400 cursor-pointer">
                  Remember me
                </label>
              </div>
              <button
                type="button"
                onClick={() => alert('Password reset functionality coming soon!')}
                className="text-gray-400 hover:text-gray-300"
              >
                Forgot Password?
              </button>
            </div>

            {error && (
              <div className="text-sm text-red-400 bg-red-900/20 p-3 rounded-lg border border-red-800/30">
                {error}
                {error.includes('Network error') && (
                  <div className="mt-2 text-xs text-gray-400">
                    Make sure the backend server is running on http://localhost:8080
                  </div>
                )}
              </div>
            )}

            <Button
              type="submit"
              className="w-full h-12"
              disabled={isLoading}
            >
              {isLoading ? 'Signing in...' : 'Sign In'}
            </Button>

            <p className="text-center text-sm text-gray-400">
              Don't have an account?{' '}
              <button
                type="button"
                onClick={() => alert('Sign up functionality coming soon!')}
                className="text-gray-300 hover:text-white font-medium"
              >
                Sign Up
              </button>
            </p>
          </form>
        </div>
      </div>

      {/* Right Panel - Image */}
      <div className="hidden lg:block lg:w-1/2 relative overflow-hidden">
        <div 
          className="absolute inset-0 bg-cover bg-center"
          style={{
            backgroundImage: 'url("https://images.unsplash.com/photo-1500595046743-cd271d694d30?q=80&w=2074")',
            filter: 'brightness(0.85)'
          }}
        />
        <div className="absolute inset-0 bg-gradient-to-br from-primary/20 to-transparent" />
      </div>
    </div>
  );
}
