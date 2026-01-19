import { 
  Container, Title, TextInput, Button, Card, Stack, 
  Group, Select, Divider, Text, Grid 
} from '@mantine/core';
import { useForm } from '@mantine/form';
import { useNavigate } from 'react-router-dom';
import { notifications } from '@mantine/notifications';
import { IconCheck } from '@tabler/icons-react';
import { managerApi } from "../../api/managerApi"; 

export function ContractCreatePage() {
  const nav = useNavigate();

  const form = useForm({
    initialValues: {
      partnerDraftId: 1001,
      domainId: 1,
      businessName: '',
      businessNumber: '',
      ceoName: '',
      contactEmail: '',
      contactPhone: '',
      settlementEmail: '',
      salesReportEmail: '',
      taxEmail: '',
      issueMethod: 'ONLINE',
    },
  });

  const handleSubmit = async (values: typeof form.values) => {
    try {
      const headers = { 'X-MANAGER-ID': '2' };

      /**
    
       * 1. 계약 Draft 생성
       */
      const res = await managerApi.post('/api/manager/contracts', values, { headers });
      const contractId = res.data;

      /**
       * 2. 생성된 ID로 승인 요청(Request) 전송
       */
      await managerApi.post(`/api/manager/contracts/${contractId}/request`, {}, { headers });
      
      notifications.show({
        title: '계약 신청 완료',
        message: '판매 계약 요청이 승인 대기 상태로 등록되었습니다.',
        color: 'green',
        icon: <IconCheck size={16} />
      });
      
      nav('/contracts');
    } catch (error: any) {
      console.error('계약 등록 실패:', error);
      const errorMsg = error.response?.data?.message || '등록 중 오류가 발생했습니다.';
      notifications.show({
        title: '등록 실패',
        message: errorMsg,
        color: 'red'
      });
    }
  };

  return (
    <Container size={1600} fluid py="xl">
      <Group justify="space-between" mb="lg">
        <Box>
          <Title order={2}>판매 계약 신청</Title>
          <Text size="sm" c="dimmed">사업자 정보 및 정산 이메일을 정확히 입력해주세요.</Text>
        </Box>
      </Group>
      
      <Card withBorder padding="xl" radius="md" shadow="sm">
        <form onSubmit={form.onSubmit(handleSubmit)}>
          <Stack gap="xl">
            
            <div>
              <Text fw={700} size="lg" mb="md" c="blue.8">🏢 사업자 기본 정보</Text>
              <Grid gutter="md">
                <Grid.Col span={{ base: 12, md: 4 }}>
                  <TextInput label="사업자명" placeholder="(주)티켓매니아" required {...form.getInputProps('businessName')} />
                </Grid.Col>
                <Grid.Col span={{ base: 12, md: 4 }}>
                  <TextInput label="사업자번호" placeholder="123-45-67890" required {...form.getInputProps('businessNumber')} />
                </Grid.Col>
                <Grid.Col span={{ base: 12, md: 4 }}>
                  <TextInput label="대표자명" placeholder="홍길동" required {...form.getInputProps('ceoName')} />
                </Grid.Col>
              </Grid>
            </div>

            <Divider />

            <div>
              <Text fw={700} size="lg" mb="md" c="blue.8">📧 연락처 및 정산 정보</Text>
              <Grid gutter="md">
                <Grid.Col span={{ base: 12, md: 6 }}>
                  <TextInput label="담당자 이메일" placeholder="manager@test.com" required {...form.getInputProps('contactEmail')} />
                </Grid.Col>
                <Grid.Col span={{ base: 12, md: 6 }}>
                  <TextInput label="담당자 전화번호" placeholder="010-1234-5678" required {...form.getInputProps('contactPhone')} />
                </Grid.Col>
                <Grid.Col span={{ base: 12, md: 4 }}>
                  <TextInput label="정산용 이메일" placeholder="settle@test.com" required {...form.getInputProps('settlementEmail')} />
                </Grid.Col>
                <Grid.Col span={{ base: 12, md: 4 }}>
                  <TextInput label="세금계산서 이메일" placeholder="tax@test.com" required {...form.getInputProps('taxEmail')} />
                </Grid.Col>
                <Grid.Col span={{ base: 12, md: 4 }}>
                  <TextInput label="매출 보고용 이메일" placeholder="report@test.com" required {...form.getInputProps('salesReportEmail')} />
                </Grid.Col>
              </Grid>
            </div>

            <Divider />

            <div>
              <Text fw={700} size="lg" mb="md" c="blue.8">⚙️ 기타 설정</Text>
              <Grid>
                <Grid.Col span={{ base: 12, md: 4 }}>
                  <Select 
                    label="발권 방식" 
                    data={[
                      { value: 'ONLINE', label: '온라인 발권' },
                      { value: 'ON_SITE', label: '현장 발권' },
                      { value: 'DELIVERY', label: '배송' }
                    ]}
                    {...form.getInputProps('issueMethod')}
                  />
                </Grid.Col>
              </Grid>
            </div>

            <Group justify="flex-end" mt="xl" pt="xl" style={{ borderTop: '1px solid #eee' }}>
              <Button variant="subtle" color="gray" onClick={() => nav(-1)}>취소</Button>
              <Button type="submit" size="md" px="xl">계약 Draft 생성 및 승인 요청</Button>
            </Group>
          </Stack>
        </form>
      </Card>
    </Container>
  );
}

// Box 컴포넌트 import 누락 방지를 위한 추가 설명
import { Box } from '@mantine/core'; 
export default ContractCreatePage;