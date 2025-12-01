// import { Title, Card, Text, Grid, Table, Badge } from "@mantine/core";
// import CounselorLayout from "../layouts/CounselorLayout";

// // Mock 상담 리스트
// const todaySessions = [
//   {
//     id: 1,
//     user: "김고객",
//     category: "배송문의",
//     start: "10:00",
//     end: "10:05",
//     status: "완료",
//   },
//   {
//     id: 2,
//     user: "박영희",
//     category: "환불요청",
//     start: "10:20",
//     end: "10:30",
//     status: "완료",
//   },
//   {
//     id: 3,
//     user: "최철수",
//     category: "계정문의",
//     start: "11:00",
//     end: "진행중",
//     status: "진행중",
//   },
// ];

// // Mock 공지사항
// const notices = [
//   { id: 1, title: "[필독] 상담 스크립트 업데이트", date: "2025-12-01" },
//   { id: 2, title: "시스템 점검 안내 (12/5)", date: "2025-12-02" },
// ];

// export default function DashboardPage() {
//   return (
//     <CounselorLayout>
//       <Title order={2} mb="lg">
//         상담사 대시보드
//       </Title>

//       <Grid>
//         {/* 📌 오늘 상담 목록 */}
//         <Grid.Col span={8}>
//           <Card withBorder shadow="sm" p="lg" mb="lg">
//             <Text fw={700} mb="md">
//               오늘 상담 목록
//             </Text>

//             <Table striped highlightOnHover>
//               <Table.Thead>
//                 <Table.Tr>
//                   <Table.Th>고객명</Table.Th>
//                   <Table.Th>카테고리</Table.Th>
//                   <Table.Th>시작</Table.Th>
//                   <Table.Th>종료</Table.Th>
//                   <Table.Th>상태</Table.Th>
//                 </Table.Tr>
//               </Table.Thead>

//               <Table.Tbody>
//                 {todaySessions.map((s) => (
//                   <Table.Tr key={s.id}>
//                     <Table.Td>{s.user}</Table.Td>
//                     <Table.Td>{s.category}</Table.Td>
//                     <Table.Td>{s.start}</Table.Td>
//                     <Table.Td>{s.end}</Table.Td>
//                     <Table.Td>
//                       {s.status === "완료" ? (
//                         <Badge color="green">완료</Badge>
//                       ) : (
//                         <Badge color="blue">진행중</Badge>
//                       )}
//                     </Table.Td>
//                   </Table.Tr>
//                 ))}
//               </Table.Tbody>
//             </Table>
//           </Card>
//         </Grid.Col>

//         {/* 📌 공지사항 패널 */}
//         <Grid.Col span={4}>
//           <Card withBorder shadow="sm" p="lg">
//             <Text fw={700} mb="md">
//               공지사항
//             </Text>

//             {notices.map((n) => (
//               <Card key={n.id} withBorder p="sm" mb="sm">
//                 <Text fw={600}>{n.title}</Text>
//                 <Text size="xs" c="dimmed">
//                   {n.date}
//                 </Text>
//               </Card>
//             ))}
//           </Card>
//         </Grid.Col>
//       </Grid>
//     </CounselorLayout>
//   );
// }
