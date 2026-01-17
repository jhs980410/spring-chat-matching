import { useState } from 'react';
import { 
  Stepper, Button, Group, TextInput, NumberInput, 
  Select, Stack, Card, Title, Text, Grid, Divider, 
  Box, Container, ActionIcon, Badge, Textarea 
} from '@mantine/core';
import { useForm } from '@mantine/form';
import { notifications } from '@mantine/notifications';
import { IconTrash, IconChevronRight, IconChevronLeft, IconDeviceFloppy } from '@tabler/icons-react';
import { managerApi as api } from '../../api/managerApi';
import type { CreateDraftRequest } from '../../type/event';
import { useNavigate, useSearchParams } from 'react-router-dom';

/**
 * 공연 도메인 전용 카테고리 (ID 11~14)
 */
const PERFORMANCE_CATEGORIES = [
  { value: '11', label: '콘서트' },
  { value: '12', label: '뮤지컬/연극' },
  { value: '13', label: '스포츠' },
  { value: '14', label: '전시/행사' },
];

export function EventCreatePage() {
  const [active, setActive] = useState(0);
  const [loading, setLoading] = useState(false);
  const [searchParams] = useSearchParams();
  const nav = useNavigate();

  // URL 파라미터에서 contractId 추출
  const contractIdFromQuery = Number(searchParams.get('contractId')) || 0;

  // 백엔드 CreateDraftRequest 구조에 1:1 매칭
  const form = useForm<CreateDraftRequest>({
    initialValues: {
      salesContractDraftId: contractIdFromQuery,
      event: {
        domainId: 1,
        categoryId: 11,
        title: '',
        description: '',
        venue: '',
        startAt: new Date().toISOString().slice(0, 16), // datetime-local input 호환용
        endAt: new Date().toISOString().slice(0, 16),
        thumbnail: '',
      },
      tickets: [
        { name: '', price: 0, totalQuantity: 0, sectionCode: '', sectionName: '', rowLabel: '' }
      ]
    },
    validate: {
      event: {
        title: (value) => (value.length < 2 ? '제목을 입력해주세요' : null),
        venue: (value) => (value.length < 2 ? '장소를 입력해주세요' : null),
      },
      tickets: {
        name: (value) => (value.length < 1 ? '등급명을 입력해주세요' : null),
      }
    }
  });

  const handleSubmit = async () => {
    setLoading(true);
    try {
      // 백엔드 LocalDateTime 포맷(ISO-8601)으로 전송
      const payload = {
        ...form.values,
        event: {
          ...form.values.event,
          startAt: new Date(form.values.event.startAt).toISOString(),
          endAt: new Date(form.values.event.endAt).toISOString(),
        }
      };

      // 8081 백엔드 컨트롤러 호출
      await api.post('/manager/drafts', payload, {
        headers: { 'X-MANAGER-ID': '2' } // 실무에선 세션/로그인 유저 ID 사용
      });
      
      notifications.show({
        title: '등록 성공',
        message: '공연 및 티켓 초안이 성공적으로 생성되었습니다.',
        color: 'green'
      });
      nav('/events');
    } catch (error) {
      console.error(error);
      notifications.show({
        title: '등록 실패',
        message: '데이터 전송 중 오류가 발생했습니다. 모든 필수 필드를 확인하세요.',
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
          <Group justify="space-between" align="flex-end">
            <Stack gap={4}>
              <Title order={2} c="blue.7">새 공연 상품 등록 (Draft)</Title>
              <Text size="sm" c="dimmed">백엔드 TicketRequestService.createDraft 규격에 맞춘 등록 페이지입니다.</Text>
            </Stack>
            {contractIdFromQuery > 0 && (
              <Badge size="xl" variant="filled" color="blue" radius="sm">
                계약 Draft ID: #{contractIdFromQuery}
              </Badge>
            )}
          </Group>
        </Box>

        <Grid gutter="xl">
          <Grid.Col span={{ base: 12, md: 9 }}>
            <Stack gap="md">
              <Card withBorder radius="md" p="md" shadow="xs">
                <Stepper active={active} size="sm" allowNextStepsSelect={false}>
                  <Stepper.Step label="공연 정보" description="장소 및 카테고리" />
                  <Stepper.Step label="티켓/좌석" description="가격 정책 설정" />
                  <Stepper.Step label="검토" description="데이터 최종 확인" />
                </Stepper>
              </Card>

              <Card withBorder radius="md" p="xl" shadow="sm" mih={650}>
                {active === 0 && (
                  <Stack gap="lg">
                    <Title order={4}>🏢 공연(Event) 정보 설정</Title>
                    <Divider />
                    <Grid>
                      <Grid.Col span={4}>
                         <NumberInput 
                          label="Sales Contract Draft ID" 
                          readOnly 
                          variant="filled"
                          {...form.getInputProps('salesContractDraftId')} 
                        />
                      </Grid.Col>
                      <Grid.Col span={8}>
                        <TextInput label="공연 제목" placeholder="공연명을 입력하세요" required {...form.getInputProps('event.title')} />
                      </Grid.Col>
                    </Grid>

                    <Group grow>
                      <Select 
                        label="카테고리" 
                        data={PERFORMANCE_CATEGORIES} 
                        value={form.values.event.categoryId.toString()}
                        onChange={(val) => form.setFieldValue('event.categoryId', Number(val))}
                        required
                      />
                      <TextInput label="공연 장소 (Venue)" placeholder="장소 입력" required {...form.getInputProps('event.venue')} />
                    </Group>

                    <Grid grow>
                      <Grid.Col span={6}>
                        <TextInput label="시작 일시" type="datetime-local" required {...form.getInputProps('event.startAt')} />
                      </Grid.Col>
                      <Grid.Col span={6}>
                        <TextInput label="종료 일시" type="datetime-local" required {...form.getInputProps('event.endAt')} />
                      </Grid.Col>
                    </Grid>
                    
                    <TextInput label="썸네일 이미지 URL" placeholder="https://..." {...form.getInputProps('event.thumbnail')} />
                    <Textarea label="상세 설명 (Description)" placeholder="공연 상세 내용을 입력하세요" minRows={5} {...form.getInputProps('event.description')} />
                  </Stack>
                )}

                {active === 1 && (
                  <Stack gap="lg">
                    <Group justify="space-between">
                      <Title order={4}>🎟️ 티켓(Ticket) 정책 설정</Title>
                      <Button variant="outline" size="xs" onClick={() => form.insertListItem('tickets', { 
                        name: '', price: 0, totalQuantity: 0, sectionCode: '', sectionName: '', rowLabel: '' 
                      })}>
                        + 티켓 등급 추가
                      </Button>
                    </Group>
                    <Divider />
                    {form.values.tickets.map((_, index) => (
                      <Stack key={index} p="md" style={{ border: '1px solid #e9ecef', borderRadius: '8px' }} bg="gray.0">
                        <Group align="flex-end">
                          <TextInput label="등급명 (ex: VIP)" style={{ flex: 2 }} {...form.getInputProps(`tickets.${index}.name`)} required />
                          <NumberInput label="가격 (Price)" thousandSeparator="," style={{ flex: 1 }} {...form.getInputProps(`tickets.${index}.price`)} required />
                          <NumberInput label="총 수량" style={{ flex: 1 }} {...form.getInputProps(`tickets.${index}.totalQuantity`)} required />
                          <ActionIcon color="red" variant="subtle" onClick={() => form.removeListItem('tickets', index)} mb={5}>
                            <IconTrash size={18} />
                          </ActionIcon>
                        </Group>
                        <Group grow>
                          <TextInput label="구역 코드 (sectionCode)" placeholder="SEC-A" {...form.getInputProps(`tickets.${index}.sectionCode`)} />
                          <TextInput label="구역명 (sectionName)" placeholder="A구역" {...form.getInputProps(`tickets.${index}.sectionName`)} />
                          <TextInput label="열 정보 (rowLabel)" placeholder="1열" {...form.getInputProps(`tickets.${index}.rowLabel`)} />
                        </Group>
                      </Stack>
                    ))}
                  </Stack>
                )}

                {active === 2 && (
                  <Stack>
                    <Title order={4}>🧐 최종 데이터 검토</Title>
                    <Text size="sm" c="dimmed">아래의 데이터가 백엔드 서비스로 전송됩니다.</Text>
                    <Box bg="dark.8" p="md" style={{ borderRadius: '8px' }}>
                      <pre style={{ fontSize: '11px', color: '#51cf66', overflow: 'auto', margin: 0 }}>
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
                <Stack gap="xs">
                  <Group justify="space-between"><Text size="xs" c="dimmed">공연 제목</Text><Text size="xs" fw={600} truncate>{form.values.event.title || '미입력'}</Text></Group>
                  <Group justify="space-between"><Text size="xs" c="dimmed">티켓 종류</Text><Text size="xs" fw={600}>{form.values.tickets.length}종</Text></Group>
                  <Group justify="space-between">
                    <Text size="xs" c="dimmed">총 발행 수량</Text>
                    <Text size="xs" fw={700} c="blue">
                      {form.values.tickets.reduce((acc, curr) => acc + (Number(curr.totalQuantity) || 0), 0).toLocaleString()}장
                    </Text>
                  </Group>
                </Stack>
              </Card>

              <Group grow>
                {active !== 0 && (
                  <Button variant="default" leftSection={<IconChevronLeft size={16}/>} onClick={() => setActive(active - 1)}>
                    이전
                  </Button>
                )}
                <Button 
                  color="blue" 
                  loading={loading}
                  rightSection={active === 2 ? <IconDeviceFloppy size={16}/> : <IconChevronRight size={16}/>}
                  onClick={() => active === 2 ? handleSubmit() : setActive(active + 1)}
                >
                  {active === 2 ? "Draft 생성 요청" : "다음"}
                </Button>
              </Group>
            </Stack>
          </Grid.Col>
        </Grid>
      </Stack>
    </Container>
  );
}