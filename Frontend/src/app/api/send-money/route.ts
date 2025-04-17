import { NextResponse } from "next/server";
import axios, { AxiosError } from "axios";
import { cookies } from "next/headers";
import config from "@/next.config";
import { endpoints } from "@/app/auth/_apis/endpoints";

export const POST = async (req: Request) => {
  try {
    const body = await req.json();
    const { senderAccountId, receiverAccountId, amount, transactionalNote } = body;

    const cookieStore = cookies();
    const token = (await cookieStore).get("token")?.value;
    const userId = (await cookieStore).get("userId")?.value;

    if (
      !token ||
      !userId ||
      !senderAccountId ||
      !receiverAccountId ||
      !amount
    ) {
      return NextResponse.json(
        { success: false, error: "Missing required fields" },
        { status: 400 }
      );
    }

    const paymentData = {
      uid: userId,
      sender_account_id: senderAccountId,
      receiver_account_id: receiverAccountId,
      transactional_note: transactionalNote ,
      amount,
    };

    const response = await axios.post(
      `${config.api.endpoints}${endpoints.sendMoney}`,
      paymentData,
      { headers: { Authorization: `Bearer ${token}` } }
    );

    return NextResponse.json({ success: true, data: response.data });
  } catch (error: unknown) {
    if (error instanceof AxiosError) {
      return NextResponse.json(
        {
          success: false,
          error:
            error.response?.data?.error ||
            error.response?.data ||
            "Internal Server Error",
        },
        { status: error.response?.status || 500 }
      );
    }

    return NextResponse.json(
      { success: false, error: "An unexpected error occurred" },
      { status: 500 }
    );
  }
};
