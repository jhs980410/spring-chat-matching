import {
  Title,
  Card,
  Text,
  Grid,
  Table,
  Badge,
  SimpleGrid,
  Divider,
} from "@mantine/core";

import {
  LineChart,
  BarChart,
  DonutChart,
} from "@mantine/charts";

import { mockStats } from "../../data/mock/mockStats";
import { mockSessions } from "../../data/mock/mockSessions";
import { mockCounselors } from "../../data/mock/mockCounselors";

// 날짜 정렬
const sortedStats = [...mockStats].sort((a, b) =>
  a.stat_date.localeCompare(b.stat_date)
);

const dailyHandled = sortedStats.map((s) => s.handled_count);
const dailyAvgDuration = sortedStats.map((s) => s.avg_duration_sec);
const dailyAvgScore = sortedStats.map((s) => s.avg_score);

// 상담사별 총 처리량
const counselorLoad = mockCounselors.map((c) => {
  const total = mockStats
    .filter((s) => s.counselor_id === c.id)
    .reduce((acc, cur) => acc + cur.handled_count, 0);

  return { counselor: c.name, count: total };
});

// 상담 상태 비율
const statusCounts = {
  WAITING: 0,
  IN_PROGRESS: 0,
  ENDED: 0,
  AFTER_CALL: 0,
};

mockSessions.forEach((s) => {
  if (statusCounts[s.status as keyof typeof statusCounts] !== undefined) {
    statusCounts[s.status as keyof typeof statusCounts]++;
  }
});

// DonutChart용 데이터
const donutData = [
  { name: "대기", value: statusCounts.WAITING, color: "#868e96" },
  { name: "진행중", value: statusCounts.IN_PROGRESS, color: "#74c0fc" },
  { name: "종료됨", value: statusCounts.ENDED, color: "#51cf66" },
  { name: "후처리", value: statusCounts.AFTER_CALL, color: "#ffd43b" },
];

// “오늘 상담 목록”
const todaySessions = [
  { id: 1, user: "김고객", category: "배송문의", start: "10:00", end: "10:05", status: "완료" },
  { id: 2, user: "박영희", category: "환불요청", start: "10:20", end: "10:30", status: "완료" },
  { id: 3, user: "최철수", category: "계정문의", start: "11:00", end: "진행중", status: "진행중" },
];

// 공지사항
const notices = [
  { id: 1, title: "[필독] 상담 스크립트 업데이트", date: "2025-12-01" },
  { id: 2, title: "시스템 점검 안내 (12/5)", date: "2025-12-02" },
];

export default function DashboardPage() {
  return (
    <>
      <Title order={2} mb="lg">
        상담사 대시보드
      </Title>

      {/* KPI 3개 */}
      <SimpleGrid cols={3} spacing="lg" mb="lg">
        <Card withBorder p="md">
          <Title order={5}>총 상담 건수</Title>
          <Text size="xl" fw="bold">
            {dailyHandled.reduce((a, b) => a + b, 0)} 건
          </Text>
        </Card>

        <Card withBorder p="md">
          <Title order={5}>평균 상담 시간</Title>
          <Text size="xl" fw="bold">
            {(dailyAvgDuration.reduce((a, b) => a + b, 0) / dailyAvgDuration.length).toFixed(1)} 초
          </Text>
        </Card>

        <Card withBorder p="md">
          <Title order={5}>평균 만족도</Title>
          <Text size="xl" fw="bold">
            {(dailyAvgScore.reduce((a, b) => a + b, 0) / dailyAvgScore.length).toFixed(2)}
          </Text>
        </Card>
      </SimpleGrid>

      <Divider my="lg" />

      {/* 차트 4종류 */}
      <Card withBorder p="lg" mb="xl">
        <Title order={4} mb="md">📈 일자별 상담 건수</Title>

        <LineChart
          h={250}
          data={sortedStats.map((s) => ({ date: s.stat_date, count: s.handled_count }))}
          dataKey="date"
          series={[{ name: "count", label: "상담 수", color: "blue" }]}
          withLegend
        />
      </Card>

      <Card withBorder p="lg" mb="xl">
        <Title order={4} mb="md">⏱ 평균 상담 시간</Title>

        <LineChart
          h={250}
          data={sortedStats.map((s) => ({ date: s.stat_date, duration: s.avg_duration_sec }))}
          dataKey="date"
          series={[{ name: "duration", label: "평균 시간(초)", color: "green" }]}
          withLegend
        />
      </Card>

      <Card withBorder p="lg" mb="xl">
        <Title order={4} mb="md">👥 상담사별 총 처리량</Title>

        <BarChart
          h={250}
          data={counselorLoad}
          dataKey="counselor"
          series={[{ name: "count", label: "건수", color: "teal" }]}
          withLegend
        />
      </Card>

      <Card withBorder p="lg" mb="xl">
        <Title order={4} mb="md">📊 상담 상태 비율</Title>

        <DonutChart withLabels withTooltip size={220} data={donutData} />
      </Card>

      {/* 오늘 상담 + 공지사항 */}
      <Grid>
        <Grid.Col span={8}>
          <Card withBorder shadow="sm" p="lg" mb="lg">
            <Text fw={700} mb="md">오늘 상담 목록</Text>

            <Table striped highlightOnHover>
              <Table.Thead>
                <Table.Tr>
                  <Table.Th>고객명</Table.Th>
                  <Table.Th>카테고리</Table.Th>
                  <Table.Th>시작</Table.Th>
                  <Table.Th>종료</Table.Th>
                  <Table.Th>상태</Table.Th>
                </Table.Tr>
              </Table.Thead>

              <Table.Tbody>
                {todaySessions.map((s) => (
                  <Table.Tr key={s.id}>
                    <Table.Td>{s.user}</Table.Td>
                    <Table.Td>{s.category}</Table.Td>
                    <Table.Td>{s.start}</Table.Td>
                    <Table.Td>{s.end}</Table.Td>
                    <Table.Td>
                      {s.status === "완료" ? (
                        <Badge color="green">완료</Badge>
                      ) : (
                        <Badge color="blue">진행중</Badge>
                      )}
                    </Table.Td>
                  </Table.Tr>
                ))}
              </Table.Tbody>
            </Table>
          </Card>
        </Grid.Col>

        {/* 공지사항 */}
        <Grid.Col span={4}>
          <Card withBorder shadow="sm" p="lg">
            <Text fw={700} mb="md">공지사항</Text>

            {notices.map((n) => (
              <Card key={n.id} withBorder p="sm" mb="sm">
                <Text fw={600}>{n.title}</Text>
                <Text size="xs" c="dimmed">{n.date}</Text>
              </Card>
            ))}
          </Card>
        </Grid.Col>
      </Grid>
    </>
  );
}
