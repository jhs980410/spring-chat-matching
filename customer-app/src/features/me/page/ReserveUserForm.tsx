import { useEffect, useState } from "react";
import {
  Box,
  Text,
  TextInput,
  Button,
  Group,
  Stack,
  Checkbox,
  Card,
  Center,
  Loader,
} from "@mantine/core";
import { useNavigate, useParams } from "react-router-dom";
import api from "../../../api/axios";

type ReserveUserFormData = {
  realName: string;
  phone: string;
  email: string;
  birth: string;
  default: boolean;
};

const emptyForm: ReserveUserFormData = {
  realName: "",
  phone: "",
  email: "",
  birth: "",
  default: false,
};

export default function ReserveUserForm() {
  const { id } = useParams<{ id: string }>();
  const isEdit = !!id;
  const navigate = useNavigate();

  const [form, setForm] = useState<ReserveUserFormData>(emptyForm);
  const [loading, setLoading] = useState(isEdit);
  const [saving, setSaving] = useState(false);

  useEffect(() => {
    if (!isEdit) return;

    const fetchDetail = async () => {
      try {
        const res = await api.get(`/me/reserve-users/${id}`);
        setForm({
          realName: res.data.realName,
          phone: res.data.phone,
          email: res.data.email,
          birth: res.data.birth,
          default: res.data.default,
        });
      } finally {
        setLoading(false);
      }
    };

    fetchDetail();
  }, [id, isEdit]);

  const handleChange = (
    key: keyof ReserveUserFormData,
    value: string | boolean
  ) => {
    setForm((prev) => ({ ...prev, [key]: value }));
  };

  const handleSubmit = async () => {
    if (!form.realName || !form.phone) {
      alert("이름과 연락처는 필수입니다.");
      return;
    }

    try {
      setSaving(true);

      if (isEdit) {
        await api.put(`/me/reserve-users/${id}`, form);
      } else {
        await api.post("/me/reserve-users", form);
      }

      navigate("/me/reserve-users");
    } finally {
      setSaving(false);
    }
  };

  if (loading) {
    return (
      <Center h={200}>
        <Loader />
      </Center>
    );
  }

  return (
    <Box>
      <Text fw={700} size="lg" mb="md">
        {isEdit ? "예매자 수정" : "예매자 추가"}
      </Text>

      <Card withBorder radius="md">
        <Stack>
          <TextInput
            label="이름"
            placeholder="실명 입력"
            value={form.realName}
            onChange={(e) =>
              handleChange("realName", e.currentTarget.value)
            }
            required
          />

          <TextInput
            label="연락처"
            placeholder="010-0000-0000"
            value={form.phone}
            onChange={(e) =>
              handleChange("phone", e.currentTarget.value)
            }
            required
          />

          <TextInput
            label="이메일"
            placeholder="example@email.com"
            value={form.email}
            onChange={(e) =>
              handleChange("email", e.currentTarget.value)
            }
          />

          <TextInput
            label="생년월일"
            placeholder="YYYY-MM-DD"
            value={form.birth}
            onChange={(e) =>
              handleChange("birth", e.currentTarget.value)
            }
          />

          <Checkbox
            label="기본 예매자로 설정"
            checked={form.default}
            onChange={(e) =>
              handleChange("default", e.currentTarget.checked)
            }
          />

          <Group justify="flex-end" mt="md">
            <Button
              variant="default"
              onClick={() => navigate(-1)}
            >
              취소
            </Button>
            <Button onClick={handleSubmit} loading={saving}>
              {isEdit ? "수정" : "추가"}
            </Button>
          </Group>
        </Stack>
      </Card>
    </Box>
  );
}
