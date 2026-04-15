import * as React from "react";

import { cn } from "./utils";

function Textarea({ className, ...props }: React.ComponentProps<"textarea">) {
  return (
    <textarea
      data-slot="textarea"
      className={cn(
        "resize-none border-[#e5e7eb] placeholder:text-[#6b7280] focus-visible:border-[#22c55e] focus-visible:ring-[#22c55e]/50 aria-invalid:ring-[#ef4444]/20 dark:aria-invalid:ring-[#ef4444]/40 aria-invalid:border-[#ef4444] flex field-sizing-content min-h-16 w-full rounded-md border bg-white px-3 py-2 text-base transition-[color,box-shadow] outline-none focus-visible:ring-[3px] disabled:cursor-not-allowed disabled:opacity-50 md:text-sm",
        className,
      )}
      {...props}
    />
  );
}

export { Textarea };
