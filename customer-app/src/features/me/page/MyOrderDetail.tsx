// me/page/MyOrderDetail.tsx
import { useParams } from "react-router-dom";

export default function MyOrderDetail() {
  const { orderId } = useParams();

  return (
    <div>
      <h2>예매 상세</h2>
      <p>주문번호: {orderId}</p>
    </div>
  );
}
