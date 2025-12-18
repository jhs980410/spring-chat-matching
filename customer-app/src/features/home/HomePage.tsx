// features/home/HomePage.tsx
import { Container, Stack } from "@mantine/core";
import { useState } from "react";

import Header from "./components/Header";
import HeroBanner from "./components/HeroBanner";
import CategoryTabs from "./components/CategoryTabs";
import FeaturedEventsSection from "./components/FeaturedEventsSection";
import RankingSection from "./components/RankingSection";
import OpenSoonSection from "./components/OpenSoonSection";

import type { Category } from "./mock/home.mock";

export default function HomePage() {
  const [category, setCategory] = useState<Category>("MUSICAL");

  return (
    <>
      <Header />
       <HeroBanner />
      <Container size="lg" py="xl">
        <Stack gap="xl">
         

          <CategoryTabs value={category} onChange={setCategory} />

          <FeaturedEventsSection category={category} />
          <RankingSection category={category} />
          <OpenSoonSection category={category} />
        </Stack>
      </Container>
    </>
  );
}
