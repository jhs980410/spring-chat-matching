declare module "stompjs" {
  export interface Message {
    body: string;
    ack: () => void;
    nack: () => void;
    headers: Record<string, string>;
  }

  export interface Client {
    connect(
      headers: Record<string, string>,
      onConnect: () => void,
      onError?: (error: string) => void
    ): void;

    subscribe(
      destination: string,
      callback: (message: Message) => void,
      headers?: Record<string, string>
    ): { id: string };

    send(
      destination: string,
      headers: Record<string, string>,
      body: string
    ): void;

    disconnect(callback?: () => void): void;
  }

  export function over(socket: any): Client;
  export default { over };
}
