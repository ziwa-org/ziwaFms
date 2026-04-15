import { useState } from 'react';
import { Sidebar } from './Sidebar';
import { Outlet, useNavigate } from 'react-router-dom';

export function Layout() {
  const [currentPage, setCurrentPage] = useState('dashboard');
  const navigate = useNavigate();

  const handlePageChange = (page: string) => {
    setCurrentPage(page);
    navigate(`/${page}`);
  };

  return (
    <div className="flex h-screen bg-background">
      <div className="flex-shrink-0">
        <Sidebar currentPage={currentPage} onPageChange={handlePageChange} />
      </div>
      <main className="flex-1 overflow-auto">
        <Outlet />
      </main>
    </div>
  );
}
