import { Outlet } from "react-router-dom";
import { Container,Flex, Box  } from "@mantine/core";
import Header from "../../../layouts/Header";
import MySidebar from "../sidebar/MySidebar";

export default function MyPageLayout() {
  return (
    <>
      <Header />
<Container
  size={1600}
  py="xl"
  px="lg"               // 좌우 여백 통제
  style={{ background: "#e6f0ff" }}
>
  <Flex gap="xl" align="flex-start">
    <Box w={240}>
      <MySidebar />
    </Box>

    <Box style={{ flex: 1 }}>
      <Outlet />
    </Box>
  </Flex>
</Container>

    </>
  );
}
