# Ziwa Dairy Farm Management System - Frontend

A modern React-based web application for managing dairy farm operations, built with TypeScript, Vite, and Tailwind CSS.

## Features

- **Authentication**: Secure login and registration with JWT tokens
- **Dashboard**: Overview of farm operations with key metrics
- **Livestock Management**: Track and manage cows, breeding records
- **Production Tracking**: Record and analyze milk production
- **Health Management**: Manage health records, vaccinations, and withdrawal periods
- **Financial Management**: Track income and expenses with analytics
- **Analytics**: Advanced analytics and production comparisons

## Tech Stack

- **Framework**: React 18.3.1 with TypeScript
- **Build Tool**: Vite 8.0.8
- **Styling**: Tailwind CSS 4.1.12
- **UI Components**: shadcn/ui (Radix UI primitives)
- **Routing**: React Router v6
- **Charts**: Recharts 2.15.2
- **Forms**: React Hook Form 7.55.0
- **HTTP Client**: Axios
- **Icons**: Lucide React
- **Notifications**: Sonner

## Getting Started

### Prerequisites

- Node.js 18+ and npm
- Backend API running on http://localhost:8080

### Installation

```bash
# Install dependencies
npm install

# Start development server
npm run dev

# Build for production
npm run build

# Preview production build
npm run preview
```

### Environment Variables

Create a `.env` file in the root directory:

```env
VITE_API_BASE_URL=http://localhost:8080
VITE_APP_NAME=Ziwa Dairy Farm Management
VITE_APP_VERSION=1.0.0
```

## Project Structure

```
frontend/
├── src/
│   ├── components/          # Reusable UI components
│   │   ├── ui/              # shadcn/ui base components
│   │   ├── layout/          # Layout components (Sidebar, Header, Layout)
│   │   ├── charts/          # Chart components
│   │   ├── forms/           # Form components
│   │   └── common/          # Common components
│   ├── pages/               # Page components
│   │   ├── auth/            # Authentication pages
│   │   ├── livestock/       # Livestock management pages
│   │   ├── production/      # Production pages
│   │   ├── health/          # Health management pages
│   │   ├── financial/       # Financial pages
│   │   └── analytics/       # Analytics pages
│   ├── hooks/               # Custom React hooks
│   ├── contexts/            # React contexts (AuthContext)
│   ├── services/            # API service layer
│   ├── types/               # TypeScript type definitions
│   ├── utils/               # Utility functions
│   ├── routes/              # Route configuration
│   ├── App.tsx              # Root component
│   ├── main.tsx             # Application entry point
│   └── index.css            # Global styles
├── public/                  # Static assets
├── .env                     # Environment variables
├── .env.example             # Environment variables template
├── vite.config.ts           # Vite configuration
├── tailwind.config.js       # Tailwind configuration
├── tsconfig.json            # TypeScript configuration
└── package.json             # Dependencies and scripts
```

## Available Scripts

- `npm run dev` - Start development server on http://localhost:3000
- `npm run build` - Build for production
- `npm run preview` - Preview production build
- `npm run lint` - Run ESLint (if configured)

## API Integration

The frontend communicates with the backend API at `http://localhost:8080`. All API requests include JWT authentication tokens in the Authorization header.

### API Services

- `authService` - Authentication (login, register, logout)
- `cowService` - Livestock management
- `productionService` - Milk production tracking
- `healthService` - Health records management
- `financialService` - Financial transactions
- `analyticsService` - Dashboard and analytics
