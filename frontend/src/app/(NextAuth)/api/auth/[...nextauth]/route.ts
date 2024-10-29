import NextAuth, { NextAuthOptions } from "next-auth";
import CredentialsProvider from "next-auth/providers/credentials";
import { api } from "@/lib/api";
import { AxiosResponse } from "axios";
import { NextRequest, NextResponse } from "next/server";

// LoginResponse 타입 정의
interface LoginResponse {
    accessToken: string;
    refreshToken: string;
}

// next-auth 타입 확장
declare module "next-auth" {
    interface Session {
        accessToken: string;
        refreshToken: string;
    }

    interface User {
        id?: string;  // id를 선택 속성으로 설정
        accessToken: string;
        refreshToken: string;
    }

    interface JWT {
        accessToken: string;
        refreshToken: string;
    }
}

const options: NextAuthOptions = {
    providers: [
        CredentialsProvider({
            name: "Credentials",
            credentials: {
                email: { label: "Email", type: "email", placeholder: "example@example.com" },
                password: { label: "Password", type: "password" },
            },
            async authorize(credentials) {
                if (!credentials?.email || !credentials.password) {
                    throw new Error("Missing credentials");
                }

                try {
                    const response: AxiosResponse<LoginResponse> = await api.post("/user/login", {
                        email: credentials.email,
                        password: credentials.password,
                    });

                    if (response.status === 200) {
                        const { accessToken, refreshToken } = response.data;
                        return {
                            id: credentials.email,
                            email: credentials.email,
                            accessToken,
                            refreshToken,
                        };
                    } else {
                        throw new Error("Invalid login credentials");
                    }
                } catch (error) {
                    console.error("Login failed:", error);
                    throw new Error("Login failed");
                }
            },
        }),
        CredentialsProvider({
            id: "kakao",
            name: "kakao",
            credentials: {
                accessToken: { label: 'Access Token', type: 'text' },
                refreshToken: { label: 'Refresh Token', type: 'text' },
            },
            async authorize(credentials) {
                const accessToken = credentials?.accessToken;
                const refreshToken = credentials?.refreshToken;
                if (accessToken && refreshToken) {
                    return {
                        id: "kakao",
                        accessToken,
                        refreshToken,
                    };
                }
                throw new Error("Invalid credentials");
            }
        }),
    ],
    session: {
        strategy: "jwt",
    },
    callbacks: {
        async redirect({ url, baseUrl }) {
            return baseUrl;
        },
        async jwt({ token, user }) {
            if (user) {
                token.accessToken = user.accessToken;
                token.refreshToken = user.refreshToken;
            }
            return token;
        },
        async session({ session, token }) {
            if (token.accessToken && token.refreshToken) {
                session.accessToken = token.accessToken as string;
                session.refreshToken = token.refreshToken as string;
            }
            return session;
        },
    },
    pages: {
        signIn: "/user/login",
    },
};

// GET 및 POST 핸들러
export async function GET(req: NextRequest) {
    return NextAuth(req as any, NextResponse.next() as any, options); // 타입 단언 제거
}

export async function POST(req: NextRequest) {
    return NextAuth(req as any, NextResponse.next() as any, options); // 타입 단언 제거
}