import {
  Title,
  Card,
  Text,
  Table,
  Badge,
  SimpleGrid,
  Divider,
  Loader,
  Center,
} from "@mantine/core";

import { LineChart, BarChart, DonutChart } from "@mantine/charts";
import { useEffect, useState } from "react";
import axios from "axios";

// =============== Axios 기본 설정 (쿠키 전달 필수) ==================
axios.defaults.withCredentials = true;

// =========================
// API 응답 타입 정의
// =========================
interface DailyStat {
  statDate: string;
  handledCount: number;
  avgDurationSec: number;
  avgScore: number;
}

interface CounselorLoad {
  counselorName: string;
  handledCount: number;
}

interface StatusRatio {
  waiting: number;
  inProgress: number;
  ended: number;
  afterCall: number;
}

interface TodaySession {
  sessionId: number;
  userName: string;
  categoryName: string;
  startedAt: string | null;
  endedAt: string | null;
  status: string;
}

export default function DashboardPage() {
  const [dailyStats, setDailyStats] = useState<DailyStat[]>([]);
  const [counselorLoad, setCounselorLoad] = useState<CounselorLoad[]>([]);
  const [statusRatio, setStatusRatio] = useState<StatusRatio | null>(null);
  const [todaySessions, setTodaySessions] = useState<TodaySession[]>([]);
  const [loading, setLoading] = useState(true);

  // =========================
  // API 호출
  // =========================
  useEffect(() => {
    async function loadDashboard() {
      try {
        const [daily, load, ratio, today] = await Promise.all([
          axios.get("/api/stats/daily"),
          axios.get("/api/stats/counselors/handled"),
          axios.get("/api/dashboard/status-ratio"),
          axios.get("/api/dashboard/sessions/today"),
        ]);

        setDailyStats(daily.data);
        setCounselorLoad(load.data);
        setStatusRatio(ratio.data);
        setTodaySessions(today.data);
      } catch (err) {
        console.error("대시보드 API 오류", err);
      } finally {
        setLoading(false);
      }
    }

    loadDashboard();
  }, []);

  if (loading)
    return (
      <Center h="80vh">
        <Loader size="xl" />
      </Center>
    );

  // =========================
  // 데이터 가공
  // =========================

  const totalHandled =
    dailyStats?.reduce((a, b) => a + (b?.handledCount ?? 0), 0) ?? 0;

  const avgDuration =
    (dailyStats?.reduce((a, b) => a + (b?.avgDurationSec ?? 0), 0) ?? 0) /
    (dailyStats.length || 1);

  const avgScore =
    (dailyStats?.reduce((a, b) => a + (b?.avgScore ?? 0), 0) ?? 0) /
    (dailyStats.length || 1);

  const donutData = statusRatio
    ? [
        { name: "대기", value: statusRatio.waiting, color: "#868e96" },
        { name: "진행중", value: statusRatio.inProgress, color: "#74c0fc" },
        { name: "종료됨", value: statusRatio.ended, color: "#51cf66" },
        { name: "후처리", value: statusRatio.afterCall, color: "#ffd43b" },
      ]
    : [];

  // ===============================
  // UI 렌더링
  // ===============================
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
            {totalHandled} 건
          </Text>
        </Card>

        <Card withBorder p="md">
          <Title order={5}>평균 상담 시간</Title>
          <Text size="xl" fw="bold">
            {avgDuration.toFixed(1)} 초
          </Text>
        </Card>

        <Card withBorder p="md">
          <Title order={5}>평균 만족도</Title>
          <Text size="xl" fw="bold">
            {avgScore.toFixed(2)}
          </Text>
        </Card>
      </SimpleGrid>

      <Divider my="lg" />

      {/* 일자별 상담 건수 */}
      <Card withBorder p="lg" mb="xl">
        <Title order={4} mb="md">📈 일자별 상담 건수</Title>
        <LineChart
          h={250}
          data={dailyStats.map((s) => ({
            date: s.statDate,
            count: s.handledCount,
          }))}
          dataKey="date"
          series={[{ name: "count", label: "상담 수", color: "blue" }]}
          withLegend
        />
      </Card>

      {/* 평균 상담 시간 */}
      <Card withBorder p="lg" mb="xl">
        <Title order={4} mb="md">⏱ 평균 상담 시간</Title>
        <LineChart
          h={250}
          data={dailyStats.map((s) => ({
            date: s.statDate,
            duration: s.avgDurationSec,
          }))}
          dataKey="date"
          series={[{ name: "duration", label: "평균 시간(초)", color: "green" }]}
          withLegend
        />
      </Card>

      {/* 상담사별 처리량 */}
      <Card withBorder p="lg" mb="xl">
        <Title order={4} mb="md">👥 상담사별 총 처리량</Title>

        <BarChart
          h={250}
          data={counselorLoad.map((c) => ({
            counselor: c.counselorName,
            count: c.handledCount,
          }))}
          dataKey="counselor"
          series={[{ name: "count", label: "건수", color: "teal" }]}
          withLegend
        />
      </Card>

      {/* 상담 상태 비율 */}
      <Card withBorder p="lg" mb="xl">
        <Title order={4} mb="md">📊 상담 상태 비율</Title>
        <DonutChart withLabels withTooltip size={220} data={donutData} />
      </Card>

      {/* 오늘 상담 목록 */}
      <Card withBorder shadow="sm" p="lg" mb="lg">
        <Text fw={700} mb="md">
          오늘 상담 목록
        </Text>

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
              <Table.Tr key={s.sessionId}>
                <Table.Td>{s.userName}</Table.Td>
                <Table.Td>{s.categoryName}</Table.Td>
                <Table.Td>{s.startedAt}</Table.Td>
                <Table.Td>{s.endedAt ?? "-"}</Table.Td>
                <Table.Td>
                  {s.status === "ENDED" ? (
                    <Badge color="green">완료</Badge>
                  ) : s.status === "IN_PROGRESS" ? (
                    <Badge color="blue">진행중</Badge>
                  ) : (
                    <Badge color="gray">{s.status}</Badge>
                  )}
                </Table.Td>
              </Table.Tr>
            ))}
          </Table.Tbody>
        </Table>
      </Card>
    </>
  );
}
