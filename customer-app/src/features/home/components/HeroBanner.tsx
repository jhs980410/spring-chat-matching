// features/home/components/HeroBanner.tsx
import {
  Box,
  Container,
  Title,
  Text,
  Button,
} from "@mantine/core";
import { Carousel } from "@mantine/carousel";
import Autoplay from "embla-carousel-autoplay";
import { homeMock } from "../mock/home.mock";
import { useRef } from "react";

export default function HeroBanner() {
  const autoplay = useRef(
    Autoplay({ delay: 4000, stopOnInteraction: false })
  );

  return (
    <Carousel
      height={360}
      loop
      plugins={[autoplay.current]}
      withIndicators
      controlSize={40}
      styles={{
        indicator: {
          width: 10,
          height: 10,
        },
      }}
    >
      {homeMock.heroBanners.map((banner) => (
        <Carousel.Slide key={banner.id}>
          <Box
            style={{
              width: "100%",
              height: "100%",
              backgroundImage: `linear-gradient(
                to right,
                rgba(0,0,0,0.65),
                rgba(0,0,0,0.2)
              ), url(${banner.imageUrl})`,
              backgroundSize: "cover",
              backgroundPosition: "center",
              display: "flex",
              alignItems: "center",
            }}
          >
            <Container size="lg">
              <Title c="white" order={1}>
                {banner.title}
              </Title>
              <Text c="gray.2" mt="sm" mb="lg">
                {banner.subtitle}
              </Text>

              <Button size="md" radius="md">
                이벤트 바로가기
              </Button>
            </Container>
          </Box>
        </Carousel.Slide>
      ))}
    </Carousel>
  );
}
