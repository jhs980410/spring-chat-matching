import { 
  AppShell, Burger, NavLink, Group, Title, Text, 
  Button, Stack 
} from '@mantine/core';
import { useDisclosure } from '@mantine/hooks';
import { IconFileText, IconPlus, IconListCheck } from '@tabler/icons-react';
import { useNavigate, Outlet, useLocation } from 'react-router-dom'; // 1. Outlet, useLocation 추가

export function ManagerLayout() { // children 프롭 제거
  const [opened, { toggle }] = useDisclosure();
  const nav = useNavigate(); 
  const location = useLocation(); // 현재 경로 확인용

  return (
    <AppShell
      header={{ height: 60 }}
      navbar={{ width: 260, breakpoint: 'sm', collapsed: { mobile: !opened } }}
      padding={0} 
    >
      <AppShell.Header p="md" style={{ borderBottom: '1px solid #e9ecef' }}>
        <Group h="100%" px="md" justify="space-between">
          <Group>
            <Burger opened={opened} onClick={toggle} hiddenFrom="sm" size="sm" />
            <Title order={4} c="blue.8" style={{ letterSpacing: '-0.5px' }}>
              NOL TICKET <Text span fw={400} c="dimmed" size="xs">MANAGER</Text>
            </Title>
          </Group>
          
          <Group gap="lg">
            <Text size="sm" fw={500} c="gray.7">테스트 기획사</Text>
            <Button size="xs" variant="light" color="red" onClick={() => nav('/login')}>
              로그아웃
            </Button>
          </Group>
        </Group>
      </AppShell.Header>

      <AppShell.Navbar p="xs" bg="#f8f9fa">
        <Stack gap="xs">
          <Text size="xs" fw={700} c="dimmed" p="xs">계약 관리</Text>
          <NavLink 
            label="계약 내역 조회" 
            leftSection={<IconListCheck size={18} />} 
            active={location.pathname === '/contracts'}
            onClick={() => nav('/contracts')} 
          />
            <NavLink 
            label="판매 계약 요청" 
            leftSection={<IconListCheck size={18} />} 
            active={location.pathname === '/contracts/create'}
            onClick={() => nav('/contracts/new')} 
          />

          
          <Text size="xs" fw={700} c="dimmed" p="xs" mt="md">상품 관리</Text>
          <NavLink 
            label="상품 신규 등록" 
            leftSection={<IconPlus size={18} />} 
            active={location.pathname === '/events/new'}
            variant="filled" 
            onClick={() => nav('/events/new')} 
          />
          <NavLink 
            label="상품 등록 내역" 
            leftSection={<IconFileText size={18} />} 
            active={location.pathname === '/events'}
            onClick={() => nav('/events')} 
          />
        </Stack>
      </AppShell.Navbar>

      <AppShell.Main bg="gray.0" style={{ minHeight: '100vh' }}>
        {/* 2. 하위 Route들이 렌더링될 위치에 Outlet 배치 */}
        <Outlet /> 
      </AppShell.Main>
    </AppShell>
  );
}