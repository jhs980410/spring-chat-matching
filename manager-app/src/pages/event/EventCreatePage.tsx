import { useState } from 'react';
import { 
  Stepper, Button, Group, TextInput, NumberInput, 
  Select, Stack, Card, Title, Text, Grid, Divider, 
  Box, Container, ActionIcon 
} from '@mantine/core';
import { useForm } from '@mantine/form';
import { notifications } from '@mantine/notifications';
import { IconTrash } from '@tabler/icons-react';
import { managerApi as api } from '../../api/managerApi';
import type { CreateDraftRequest } from '../../type/event';
import {useNavigate } from 'react-router-dom';

export function EventCreatePage() {
  const [active, setActive] = useState(0);
  const [loading, setLoading] = useState(false);
  const nav = useNavigate();

  const form = useForm<CreateDraftRequest>({
    initialValues: {
      salesContractDraftId: 0,
      event: {
        domainId: 1,
        title: '',
        categoryId: 1, // 초기값을 숫자로 설정
        description: '',
        venue: '',
        startAt: new Date().toISOString(),
        endAt: new Date().toISOString(),
        thumbnail: '',
      },
      tickets: [{ name: '', price: 0, totalQuantity: 0, sectionCode: '', sectionName: '', rowLabel: '' }]
    }
  });

  const handleSubmit = async () => {
    setLoading(true);
    try {
      // 8081 백엔드(image_607e02 스펙)로 데이터 전송
      await api.post('/manager/drafts', form.values);
      
      notifications.show({
        title: '등록 성공',
        message: '상품 초안이 성공적으로 등록되었습니다.',
        color: 'green'
      });
      nav('/events');
    } catch (error) {
      notifications.show({
        title: '등록 실패',
        message: '데이터 전송 중 오류가 발생했습니다.',
        color: 'red'
      });
    } finally {
      setLoading(false);
    }
  };

  return (
    <Container size={1600} fluid py="xl">
      <Stack gap="xl">
        <Box>
          <Title order={2} mb="xs" c="blue.7">상품 신규 등록</Title>
          <Text size="sm" c="dimmed">백엔드(8081) Draft 생성 API 규격에 맞춘 와이드 레이아웃입니다.</Text>
        </Box>

        <Grid gutter="xl">
          <Grid.Col span={{ base: 12, md: 9 }}>
            <Stack gap="md">
              <Card withBorder radius="md" p="md" shadow="xs">
                <Stepper active={active} size="sm" allowNextStepsSelect={false}>
                  <Stepper.Step label="기초 정보" description="공연 기본 설정" />
                  <Stepper.Step label="티켓 설정" description="가격 및 수량" />
                  <Stepper.Step label="최종 확인" description="데이터 검토" />
                </Stepper>
              </Card>

              <Card withBorder radius="md" p="xl" shadow="sm" mih={650}>
                {active === 0 && (
                  <Stack gap="lg">
                    <Title order={4}>공연 기본 정보</Title>
                    <Select 
                      label="판매 계약 선택" 
                      data={[{ value: '1001', label: '1001 - 더원 콘서트' }]}
                      onChange={(val) => form.setFieldValue('salesContractDraftId', Number(val))}
                    />
                    <TextInput label="공연명" placeholder="공연 제목 입력" {...form.getInputProps('event.title')} />
                    <Group grow>
                      <Select 
                        label="카테고리" 
                        data={[{ value: '1', label: '콘서트' }]} 
                        onChange={(val) => form.setFieldValue('event.categoryId', Number(val))}
                      />
                      <TextInput label="장소" {...form.getInputProps('event.venue')} />
                    </Group>
                    <TextInput label="이미지 URL" {...form.getInputProps('event.thumbnail')} />
                  </Stack>
                )}

                {active === 1 && (
                  <Stack gap="lg">
                    <Group justify="space-between">
                      <Title order={4}>티켓/좌석 상세</Title>
                      {/* 필수 필드를 모두 포함하여 등급 추가 (image_6b63cd 에러 해결) */}
                      <Button variant="light" size="xs" onClick={() => form.insertListItem('tickets', { 
                        name: '', price: 0, totalQuantity: 0, sectionCode: '', sectionName: '', rowLabel: '' 
                      })}>
                        + 등급 추가
                      </Button>
                    </Group>
                    <Divider />
                    {form.values.tickets.map((_, index) => (
                      <Stack key={index} p="sm" style={{ border: '1px solid #eee', borderRadius: '8px' }}>
                        <Group align="flex-end">
                          <TextInput label="등급" style={{ flex: 2 }} {...form.getInputProps(`tickets.${index}.name`)} />
                          <NumberInput label="가격" style={{ flex: 1 }} {...form.getInputProps(`tickets.${index}.price`)} />
                          <NumberInput label="수량" style={{ flex: 1 }} {...form.getInputProps(`tickets.${index}.totalQuantity`)} />
                          <ActionIcon color="red" variant="subtle" onClick={() => form.removeListItem('tickets', index)} mb={5}>
                            <IconTrash size={18} />
                          </ActionIcon>
                        </Group>
                        <Group grow>
                          <TextInput label="구역 코드" placeholder="A1" {...form.getInputProps(`tickets.${index}.sectionCode`)} />
                          <TextInput label="구역명" placeholder="1층 좌측" {...form.getInputProps(`tickets.${index}.sectionName`)} />
                          <TextInput label="열 정보" placeholder="A열" {...form.getInputProps(`tickets.${index}.rowLabel`)} />
                        </Group>
                      </Stack>
                    ))}
                  </Stack>
                )}

                {active === 2 && (
                  <Stack>
                    <Title order={4}>최종 데이터 확인</Title>
                    <Box bg="gray.0" p="md" style={{ borderRadius: '8px', border: '1px solid #dee2e6' }}>
                      <pre style={{ fontSize: '12px', overflow: 'auto', margin: 0 }}>
                        {JSON.stringify(form.values, null, 2)}
                      </pre>
                    </Box>
                  </Stack>
                )}
              </Card>
            </Stack>
          </Grid.Col>

          <Grid.Col span={{ base: 12, md: 3 }}>
            <Stack gap="md" style={{ position: 'sticky', top: '20px' }}>
              <Card withBorder radius="md" p="lg" shadow="sm" bg="blue.0">
                <Text fw={700} size="md" mb="md" c="blue.9">입력 요약</Text>
                <Divider mb="md" />
                <Stack gap="sm">
                  <Group justify="space-between"><Text size="sm" c="dimmed">제목</Text><Text size="sm" fw={600}>{form.values.event.title || '-'}</Text></Group>
                  <Group justify="space-between"><Text size="sm" c="dimmed">티켓 종류</Text><Text size="sm" fw={600}>{form.values.tickets.length}종</Text></Group>
                  <Group justify="space-between">
                    <Text size="sm" c="dimmed">총 수량</Text>
                    <Text size="sm" fw={600}>{form.values.tickets.reduce((acc, curr) => acc + (Number(curr.totalQuantity) || 0), 0).toLocaleString()}장</Text>
                  </Group>
                </Stack>
              </Card>

              <Group grow>
                {active !== 0 && <Button variant="default" onClick={() => setActive(active - 1)}>이전</Button>}
                <Button 
                  color="blue" 
                  loading={loading}
                  onClick={() => active === 2 ? handleSubmit() : setActive(active + 1)}
                >
                  {active === 2 ? "상품 등록 요청" : "다음 단계"}
                </Button>
              </Group>
            </Stack>
          </Grid.Col>
        </Grid>
      </Stack>
    </Container>
  );
}