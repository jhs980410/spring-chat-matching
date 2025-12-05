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
import { useEffect, useMemo, useState } from "react";
import api from "../../api/axios";

// =========================
// 공통 타입 정의
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
  const [loading, setLoading] = useState(true);
  const [dailyStats, setDailyStats] = useState<DailyStat[]>([]);
  const [counselorLoad, setCounselorLoad] = useState<CounselorLoad[]>([]);
  const [statusRatio, setStatusRatio] = useState<StatusRatio | null>(null);
  const [todaySessions, setTodaySessions] = useState<TodaySession[]>([]);

  // =========================
  // API 호출 (모듈 기능처럼 작동)
  // =========================
  useEffect(() => {
    (async () => {
      try {
        const [daily, load, ratio, today] = await Promise.all([
          api.get("/stats/daily"),
          api.get("/stats/counselors/handled"),
          api.get("/dashboard/status-ratio"),
          api.get("/dashboard/sessions/today"),
        ]);

        setDailyStats(daily.data);
        setCounselorLoad(load.data);
        setStatusRatio(ratio.data);
        setTodaySessions(today.data);
      } finally {
        setLoading(false);
      }
    })();
  }, []);

  // =========================
  // 계산값은 useMemo 처리 (렌더링 최적화)
  // =========================
  const totalHandled = useMemo(
    () => dailyStats.reduce((acc, v) => acc + v.handledCount, 0),
    [dailyStats]
  );

  const avgDuration = useMemo(
    () =>
      dailyStats.length
        ? (
            dailyStats.reduce((acc, v) => acc + v.avgDurationSec, 0) /
            dailyStats.length
          ).toFixed(1)
        : "0.0",
    [dailyStats]
  );

  const avgScore = useMemo(
    () =>
      dailyStats.length
        ? (
            dailyStats.reduce((acc, v) => acc + v.avgScore, 0) /
            dailyStats.length
          ).toFixed(2)
        : "0.00",
    [dailyStats]
  );

  const donutData = useMemo(
    () =>
      statusRatio
        ? [
            { name: "대기", value: statusRatio.waiting, color: "#868e96" },
            { name: "진행중", value: statusRatio.inProgress, color: "#74c0fc" },
            { name: "종료됨", value: statusRatio.ended, color: "#51cf66" },
            { name: "후처리", value: statusRatio.afterCall, color: "#ffd43b" },
          ]
        : [],
    [statusRatio]
  );

  // =========================
  // LOADING
  // =========================
  if (loading)
    return (
      <Center h="80vh">
        <Loader size="xl" />
      </Center>
    );

  // =========================
  // UI 렌더링
  // =========================
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
            {avgDuration} 초
          </Text>
        </Card>

        <Card withBorder p="md">
          <Title order={5}>평균 만족도</Title>
          <Text size="xl" fw="bold">
            {avgScore}
          </Text>
        </Card>
      </SimpleGrid>

      <Divider my="lg" />

      {/* 📈 일자별 상담 건수 */}
      <Card withBorder p="lg" mb="xl">
        <Title order={4} mb="md">
          📈 일자별 상담 건수
        </Title>
        <LineChart
          h={250}
          data={dailyStats.map((v) => ({ date: v.statDate, count: v.handledCount }))}
          dataKey="date"
          series={[{ name: "count", label: "상담 수", color: "blue" }]}
        />
      </Card>

      {/* ⏱ 평균 상담 시간 */}
      <Card withBorder p="lg" mb="xl">
        <Title order={4} mb="md">
          ⏱ 평균 상담 시간
        </Title>
        <LineChart
          h={250}
          data={dailyStats.map((v) => ({ date: v.statDate, duration: v.avgDurationSec }))}
          dataKey="date"
          series={[{ name: "duration", label: "평균 시간(초)", color: "green" }]}
        />
      </Card>

      {/* 👥 상담사별 처리량 */}
      <Card withBorder p="lg" mb="xl">
        <Title order={4} mb="md">
          👥 상담사별 총 처리량
        </Title>
        <BarChart
          h={250}
          data={counselorLoad.map((v) => ({
            counselor: v.counselorName,
            count: v.handledCount,
          }))}
          dataKey="counselor"
          series={[{ name: "count", label: "건수", color: "teal" }]}
        />
      </Card>

      {/* 📊 상담 상태 비율 */}
      <Card withBorder p="lg" mb="xl">
        <Title order={4} mb="md">
          📊 상담 상태 비율
        </Title>
        <DonutChart withLabels withTooltip size={220} data={donutData} />
      </Card>

      {/* 📅 오늘 상담 목록 */}
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
                <Table.Td>{s.startedAt ?? "-"}</Table.Td>
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
