import { Outlet } from "react-router-dom";
import { Container,Flex, Box  } from "@mantine/core";
import Header from "../../../layouts/Header";
import MySidebar from "../sidebar/MySidebar";

export default function MyPageLayout() {
  return (
    <>
      <Header />
<Container size={1600} py="xl" px="lg">
  <Flex gap="xl" align="flex-start">
    <Box w={240}>
      <MySidebar />
    </Box>

    <Box
      style={{
        flex: 1,
        background: "#f5f6f8",
        padding: "24px",
        borderRadius: 8,
      }}
    >
      <Outlet />
    </Box>
  </Flex>
</Container>


    </>
  );
}
