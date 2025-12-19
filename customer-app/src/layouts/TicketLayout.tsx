// layouts/TicketLayout.tsx
import { Outlet } from "react-router-dom";
import { Box } from "@mantine/core";
import Header from "./Header";
import TicketSubNav from "./TicketSubNav";

export default function TicketLayout() {
  return (
    <>
      <Header />
      <TicketSubNav />

      <Box bg="#f8f9fa" py="xl">
        <Outlet />
      </Box>
    </>
  );
}
