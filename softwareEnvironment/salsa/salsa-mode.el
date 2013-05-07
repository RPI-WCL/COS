;;; salsa-mode.el --- major mode for editing SALSA code


;; Author: Wei Huang <huangw5@cs.rpi.edu>
;; Keywords: languages salsa 

;; This is free software; you can redistribute it and/or modify it
;; under the terms of the GNU General Public License as published by
;; the Free Software Foundation; either version 2, or (at your option)
;; any later version.

;; This is distributed in the hope that it will be useful, but WITHOUT
;; ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
;; or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public
;; License for more details.

;; You should have received a copy of the GNU General Public License
;; along with GNU Emacs; see the file COPYING.  If not, write to the
;; Free Software Foundation, Inc., 59 Temple Place - Suite 330,
;; Boston, MA 02111-1307, USA.

;;; Commentary:

;; salsa-mode is a basic major mode for editing code conforming to the
;; SALSA1.x. 

;; BECAREFUL: This salsa-mode is a simple extension from java-mode

;; See also:
;; http://wcl.cs.rpi.edu/salsa/

;;; History:

;; 2010-04-05 Initial release.

;;; Code:

(require 'font-lock)
(require 'cc-mode)
(eval-when-compile
  (require 'regexp-opt))

(defconst salsa-mode-version "1.1"
  "SALSA Mode version number.")

(defgroup salsa nil
  "Major mode for editing SALSA code."
  :group 'languages
  :prefix "salsa-")

(defcustom salsa-mode-hook nil
  "Hook for customizing `salsa-mode'."
  :group 'salsa
  :type 'hook)

(defvar salsa-mode-map (c-make-inherited-keymap)
  "Keymap used in `salsa-mode' buffers.")

;;;###autoload
(define-derived-mode salsa-mode java-mode "SALSA"
  "Major mode for editing SALSA code.

This mode is derived from `java-mode'; see its documentation for further
information.

\\{salsa-mode-map}"
  (make-local-variable 'font-lock-defaults)
  (setq font-lock-defaults
        '((;; comment out the lines below to adjust
           ;; syntax highlighting gaudiness
           java-font-lock-keywords-1
           ;; java-font-lock-keywords-3
           salsa-font-lock-keywords-2
           salsa-font-lock-keywords-3
           )
          nil nil ((?_ . "w") (?$ . "w")) nil))

  (easy-menu-define c-salsa-menu salsa-mode-map
    "SALSA Mode Commands" (c-mode-menu "SALSA"))
  (set (make-local-variable 'indent-line-function) 'salsa-indent-line)
  )

(defvar salsa-font-lock-default-face 'salsa-font-lock-default-face)

(defconst salsa-font-lock-keywords-2
  (append
   java-font-lock-keywords-2
   (list

    '("\\<\\(behavior\\|module\\)\\>"
      (1 font-lock-keyword-face t)
      (2 font-lock-function-name-face nil t))

    ;; ;; need to fix this to handle: var a, b;
    ;; '("\\<\\(var\\)\\>\\(?:\\s-+\\(\\sw+\\)\\)?"
    ;;   (1 font-lock-keyword-face t)
    ;;   (2 font-lock-variable-name-face nil t))
    ))
  "Medium level highlighting for SALSA mode.")


(defconst salsa-font-lock-keywords-3
  (append
   java-font-lock-keywords-3
   (list

    '("\\<\\(currentContinuation\\|join\\)\\>"
      (1 font-lock-keyword-face t)
      (2 font-lock-function-name-face nil t))
    ))
  "Gaudy level highlighting for SALSA mode.")

(provide 'salsa-mode)

(defun salsa-indent-line ()
  "Indent current line as SALSA code"
  (interactive)
  ;; (c-indent-line)
  ;; (beginning-of-line)
	(let ((not-indented t) cur-indent)
    (progn
			(save-excursion
			  (forward-line -1)
        (end-of-line)
        ;; (message "char-before: %s" (char-before))
        (setq is-at (equal (char-before) 64)) ;; 64 is @
        (setq is-semicolon (equal (char-before) 59)) ;; 59 is ;
        (if (or is-at is-semicolon)
            (progn
              (setq cur-indent (current-indentation))
              (setq not-indented nil)
              )
          )
        )
      (if not-indented 
          (c-indent-line)
        (indent-line-to cur-indent))
      )
    )
  )


;;; salsa-mode.el ends here
