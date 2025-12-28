import { useNavigate } from "react-router-dom";
import { Button } from "@mantine/core";
import styles from "./PaymentSuccessPage.module.css";

export default function PaymentSuccessPage() {
  const navigate = useNavigate();

  return (
    <div className={styles.wrapper}>
      <h1 className={styles.title}>결제가 완료되었습니다 🎉</h1>

      <p className={styles.desc}>
        예매가 정상적으로 완료되었습니다.
        <br />
        마이페이지에서 예매 내역을 확인하실 수 있습니다.
      </p>

      <div className={styles.buttons}>
        <Button onClick={() => navigate("/me/orders")}>
          예매 내역 보기
        </Button>
        <Button variant="light" onClick={() => navigate("/")}>
          홈으로 가기
        </Button>
      </div>
    </div>
  );
}
