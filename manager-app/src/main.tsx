import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import { BrowserRouter } from 'react-router-dom' // 1. Router 임포트 추가
import './index.css'
import App from './App.tsx'

createRoot(document.getElementById('root')!).render(
  <StrictMode>
    {/* 2. App을 BrowserRouter로 감싸줍니다. */}
    <BrowserRouter> 
      <App />
    </BrowserRouter>
  </StrictMode>,
)