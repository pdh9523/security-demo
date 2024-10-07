import NextAuth, { NextAuthOptions } from "next-auth";
import CredentialsProvider from "next-auth/providers/credentials";
import { api } from "@/lib/api";
import { AxiosResponse } from "axios";
import {NextApiRequest, NextApiResponse} from "next";

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
                        console.log(response.data);
                        // 로그인 성공 시 accessToken과 refreshToken을 반환
                        return {
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
                    return { accessToken, refreshToken };
                }
                throw new Error("Invalid credentials");
            }
            }
        ),
    ],
      session: {
        strategy: "jwt",
    },
    callbacks: {
        async redirect({ url, baseUrl }) {
            // 중복 인코딩을 방지하고 기본 URL로 리디렉션
            return baseUrl;
        },

        async jwt({ token, user }) {
            // user가 있을 때만 accessToken과 refreshToken을 추가
            if (user) {
                token.accessToken = user.accessToken;
                token.refreshToken = user.refreshToken;
            }
            return token;
        },
        async session({ session, token }) {
            // token에서 accessToken과 refreshToken을 가져와서 session에 추가
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

const handler = (req: NextApiRequest, res: NextApiResponse) => {
    if (req.method === "GET" && req.url?.includes("/api/auth/callback/kakao")) {
        // /done으로 GET 요청 시 처리
        // accessToken과 refreshToken을 받아서 NextAuth에 저장
        const { accessToken, refreshToken } = req.query;

        if (accessToken && refreshToken) {
            return NextAuth(req, res, {
                ...options,
                callbacks: {
                    ...options.callbacks,
                    async jwt({ token }) {
                        token.accessToken = accessToken;
                        token.refreshToken = refreshToken;
                        return token;
                    },
                },
            });
        }
    }
    return NextAuth(req, res, options);
};

export { handler as GET, handler as POST };
