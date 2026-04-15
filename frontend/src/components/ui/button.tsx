import * as React from "react";
import { Slot } from "@radix-ui/react-slot";
import { cva, type VariantProps } from "class-variance-authority";

import { cn } from "./utils";

const buttonVariants = cva(
  "inline-flex items-center justify-center gap-2 whitespace-nowrap rounded-md text-sm font-medium transition-all disabled:pointer-events-none disabled:opacity-50 [&_svg]:pointer-events-none [&_svg:not([class*='size-'])]:size-4 shrink-0 [&_svg]:shrink-0 outline-none focus-visible:border-[#22c55e] focus-visible:ring-[#22c55e]/50 focus-visible:ring-[3px] aria-invalid:ring-[#ef4444]/20 dark:aria-invalid:ring-[#ef4444]/40 aria-invalid:border-[#ef4444]",
  {
    variants: {
      variant: {
        default: "bg-[#22c55e] text-white hover:bg-[#16a34a]",
        destructive:
          "bg-[#ef4444] text-white hover:bg-[#dc2626] focus-visible:ring-[#ef4444]/20 dark:focus-visible:ring-[#ef4444]/40 dark:bg-[#b91c1c]",
        outline:
          "border border-[#e5e7eb] bg-white text-[#1a1a1a] hover:bg-[#dcfce7] hover:text-[#166534] dark:border-[#334155] dark:bg-[#1e293b] dark:text-[#f8fafc] dark:hover:bg-[#334155]",
        secondary:
          "bg-[#f1f5f4] text-[#1a1a1a] hover:bg-[#e2e8e4] dark:bg-[#334155] dark:text-[#f8fafc]",
        ghost:
          "hover:bg-[#dcfce7] hover:text-[#166534] dark:hover:bg-[#1e3a2e]",
        link: "text-[#22c55e] underline-offset-4 hover:underline",
      },
      size: {
        default: "h-9 px-4 py-2 has-[>svg]:px-3",
        sm: "h-8 rounded-md gap-1.5 px-3 has-[>svg]:px-2.5",
        lg: "h-10 rounded-md px-6 has-[>svg]:px-4",
        icon: "size-9 rounded-md",
      },
    },
    defaultVariants: {
      variant: "default",
      size: "default",
    },
  },
);

function Button({
  className,
  variant,
  size,
  asChild = false,
  ...props
}: React.ComponentProps<"button"> &
  VariantProps<typeof buttonVariants> & {
    asChild?: boolean;
  }) {
  const Comp = asChild ? Slot : "button";

  return (
    <Comp
      data-slot="button"
      className={cn(buttonVariants({ variant, size, className }))}
      {...props}
    />
  );
}

export { Button, buttonVariants };
