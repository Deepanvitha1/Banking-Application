import { NextResponse } from "next/server";
import axios from "axios";
import { cookies } from "next/headers";
import config from "@/next.config";
import { endpoints } from "@/app/auth/_apis/endpoints";

export const GET = async (req: Request) => {
  try {
    const cookieStore = cookies();
    const token = (await cookieStore).get("token")?.value;
    const userId = (await cookieStore).get("userId")?.value;

    if (!token || !userId) {
      return NextResponse.json(
        { success: false, error: "Unauthorized" },
        { status: 401 }
      );
    }

    const { searchParams } = new URL(req.url);
    const accountType = searchParams.get("account_type");
    const page = searchParams.get("page") || "0";

    const response = await axios.get(
      `${config.api.endpoints}${endpoints.transactionHistory}`,
      {
        headers: { Authorization: `Bearer ${token}` },
        params: {
          uid: userId,
          account_type: accountType,
          page,
          size: 10,
        },
      }
    );

    return NextResponse.json({ success: true, data: response.data });
  } catch (error) {
    return NextResponse.json(
      {
        success: false,
        error: "Failed to fetch transactions" + error,
      },
      { status: 500 }
    );
  }
};
