import { AppShell, Burger, NavLink, Group, Title, Text, Button, Stack } from '@mantine/core';
import { useDisclosure } from '@mantine/hooks';
import { IconClipboardCheck, IconRocket, IconLogout } from '@tabler/icons-react';
import { useNavigate, Outlet, useLocation } from 'react-router-dom';

export function AdminLayout() {
  const [opened, { toggle }] = useDisclosure();
  const nav = useNavigate();
  const location = useLocation();

  return (
    <AppShell
      header={{ height: 60 }}
      navbar={{ width: 280, breakpoint: 'sm', collapsed: { mobile: !opened } }}
      padding="md"
    >
      <AppShell.Header p="md">
        <Group h="100%" px="md" justify="space-between">
          <Group>
            <Burger opened={opened} onClick={toggle} hiddenFrom="sm" size="sm" />
            <Title order={4} c="blue.9">
              NOL TICKET <Text span fw={900} c="orange.6">HQ ADMIN</Text>
            </Title>
          </Group>
          <Group>
            <Text size="sm" fw={600}>본사 관리자 시스템</Text>
            <Button variant="subtle" color="gray" size="xs" leftSection={<IconLogout size={14} />}>Logout</Button>
          </Group>
        </Group>
      </AppShell.Header>

      <AppShell.Navbar p="xs">
        <Stack gap="xs">
          <Text size="xs" fw={700} c="dimmed" p="xs">검토 및 승인</Text>
          <NavLink 
            label="승인 대기 목록" 
            leftSection={<IconClipboardCheck size={20} />} 
            active={location.pathname === '/hq/approvals'}
            onClick={() => nav('/hq/approvals')}
          />
          <Text size="xs" fw={700} c="dimmed" p="xs" mt="md">운영 반영</Text>
          <NavLink 
            label="발행(Publish) 관리" 
            leftSection={<IconRocket size={20} />} 
            active={location.pathname === '/hq/publish'}
            onClick={() => nav('/hq/publish')}
          />
        </Stack>
      </AppShell.Navbar>

      <AppShell.Main bg="gray.0">
        <Outlet /> 
      </AppShell.Main>
    </AppShell>
  );
}