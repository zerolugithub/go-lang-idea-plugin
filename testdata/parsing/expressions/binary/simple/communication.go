package main
var e = a + <- b
/**-----
Go file
  PackageDeclaration(main)
    PsiElement(KEYWORD_PACKAGE)('package')
    PsiWhiteSpace(' ')
    PsiElement(IDENTIFIER)('main')
  PsiWhiteSpace('\n')
  VarDeclarationsImpl
    PsiElement(KEYWORD_VAR)('var')
    PsiWhiteSpace(' ')
    VarDeclarationImpl
      LiteralIdentifierImpl
        PsiElement(IDENTIFIER)('e')
      PsiWhiteSpace(' ')
      PsiElement(=)('=')
      PsiWhiteSpace(' ')
      AdditiveExpressionImpl
        LiteralExpressionImpl
          LiteralIdentifierImpl
            PsiElement(IDENTIFIER)('a')
        PsiWhiteSpace(' ')
        PsiElement(+)('+')
        PsiWhiteSpace(' ')
        UnaryExpressionImpl
          PsiElement(<-)('<-')
          PsiWhiteSpace(' ')
          LiteralExpressionImpl
            LiteralIdentifierImpl
              PsiElement(IDENTIFIER)('b')
