import { MantineProvider, createTheme } from '@mantine/core';
import '@mantine/core/styles.css'; 
import '@mantine/notifications/styles.css'; // 알림(Notifications) 스타일 추가
import { Notifications } from '@mantine/notifications';
import {  Routes, Route, Navigate } from 'react-router-dom';

import { ManagerLayout } from './components/layout/ManagerLayout';
import { EventCreatePage } from './pages/event/EventCreatePage';
import { EventListPage } from './pages/event/EventListPage'; // 추가
import { ContractListPage } from './pages/contract/ContractListPage'; // 추가
import { ContractCreatePage } from './pages/contract/ContractCreatePage';

const theme = createTheme({
  primaryColor: 'blue',
  defaultRadius: 'md',
});
// App.tsx 수정 후
function App() {
  return (
    <MantineProvider theme={theme}>
      <Notifications position="top-right" />
      {/* <BrowserRouter> 제거! */}
        <Routes>
          <Route element={<ManagerLayout children={<div />} />}>
            <Route path="/" element={<Navigate to="/events" replace />} />
            <Route path="/events" element={<EventListPage />} />
            <Route path="/events/new" element={<EventCreatePage />} />
            <Route path="/contracts" element={<ContractListPage />} />
            <Route path="/contracts/new" element={<ContractCreatePage />} />
          </Route>
        </Routes>
      {/* </BrowserRouter> 제거! */}
    </MantineProvider>
  );
}

export default App;